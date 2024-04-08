package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRequest;
import com.api.concert.controller.queue.dto.QueueResponse;
import com.api.concert.global.common.exception.AlreadyWaitingUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final IQueueRepository iQueueRepository;

    private final long QUEUE_EXPIRED_TIME = 1L;
    private final int QUEUE_LIMIT = 5;

    public QueueResponse register(QueueRequest queueRequestDto){
        Long userId = queueRequestDto.getUserId();
        isUserAlreadyRegistered(userId);

        Queue queue = createQueue(userId);
        Queue savedQueue = iQueueRepository.save(QueueConverter.toEntity(queue));
        return QueueConverter.toResponse(savedQueue);
    }

    public void isUserAlreadyRegistered(Long userId) {
        log.info("userId : {}", userId);
        if(iQueueRepository.existsByUserIdAndStatusIsOngoingOrWaiting(userId)){
            throw new AlreadyWaitingUserException();
        }
    }

    public Queue createQueue(Long userId) {
        Queue queue = Queue.of(userId);
        long ongoingCount = iQueueRepository.getCountOfOngoingStatus();
        queue.updateStatusForOngoingCount(ongoingCount, QUEUE_LIMIT);
        return queue;
    }

    @Transactional
    @Scheduled(cron = "${queue.scan-time}")
    public void updateStatusToDoneAndOngoing(){
        List<Queue> expiredOngoingStatus = iQueueRepository.getExpiredOngoingStatus();
        if(expiredOngoingStatus.size() > 0){
            updateStatusToDone(expiredOngoingStatus);
            updateStatusToOngoingForWaitQueues();
        }
    }

    public void updateStatusToDone(List<Queue> queues) {
        List<Long> updateIds = new ArrayList<>();
        queues.forEach( queue -> {
            queue.updateStatusToDone();
            updateIds.add(queue.getConcertWaitingId());
        });
        iQueueRepository.updateStatusToDone(updateIds);
    }

    public void updateStatusToOngoingForWaitQueues() {
        int availableQueueSpace = calculateAvailableQueueSpace();

        List<Queue> queuesInWaitStatus = iQueueRepository.getQueuesInWaitStatus(availableQueueSpace);
        queuesInWaitStatus.forEach(Queue::updateStatusToOngoingAndExpiredAt);

        iQueueRepository.updateStatusToOngoing(
                queuesInWaitStatus.stream()
                        .map(QueueConverter::toEntity)
                        .toList()
        );
    }

    public int calculateAvailableQueueSpace() {
        long currentOngoingCount = iQueueRepository.getCountOfOngoingStatus();
        return (int) (QUEUE_LIMIT - currentOngoingCount);
    }

    /*public void updateStatusQueues(List<Queue> queues) {
        iQueueRepository.updateStatusQueues(queues.stream().map(QueueConverter::toEntity).toList());
    }*/

}
