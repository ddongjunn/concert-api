package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRequest;
import com.api.concert.controller.queue.dto.QueueResponse;
import com.api.concert.global.common.exception.AlreadyWaitingUserException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.api.concert.domain.queue.QueueOption.QUEUE_EXPIRED_TIME;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final QueueOption queueOption;
    private final IQueueRepository iQueueRepository;

    @PostConstruct
    public void initializeQueueCount(){
        queueOption.initializeQueueCount(iQueueRepository.getCountOfOngoingStatus());
    }

    // 대기열 등록
    @Transactional
    public QueueResponse register(QueueRequest queueRequestDto){
        Long userId = queueRequestDto.getUserId();
        isUserAlreadyRegistered(userId); //대기열에 WAIT, ONGOING 상태인 userId 존재 확인

        Queue queue = Queue.builder().userId(userId).build();
        assignQueueStatus(queue);

        return QueueConverter.toResponse(
                iQueueRepository.save(QueueConverter.toEntity(queue))
        );
    }

    public void isUserAlreadyRegistered(Long userId) {
        if(iQueueRepository.existsByUserIdAndStatusIsOngoingOrWaiting(userId)){
            throw new AlreadyWaitingUserException();
        }
    }

    public void assignQueueStatus(Queue queue) {
        if(queueOption.hasAvailableSpaceInOngoingQueue()) {
            queue.toOngoing(QUEUE_EXPIRED_TIME);
        } else {
            queue.toWait();
        }
    }

    @Transactional
    @Scheduled(cron = "${queue.scan-time}")
    public void expiredOngoingStatusToDone(){
        List<Queue> expiredOngoingStatus = iQueueRepository.getExpiredOngoingStatus();
        if(!expiredOngoingStatus.isEmpty()){
            updateStatusToDone(expiredOngoingStatus);
            updateStatusToOngoingForWaitQueues();
        }
    }

    public void updateStatusToDone(List<Queue> queues) {
        List<Long> updateIds = new ArrayList<>();
        queues.forEach( queue -> {
            queue.toDone();
            queueOption.decrementOngoingCount();
            updateIds.add(queue.getConcertWaitingId());
        });
        iQueueRepository.updateStatusToDone(updateIds);
    }

    public void updateStatusToOngoingForWaitQueues() {
        int availableQueueSpace = queueOption.calculateAvailableQueueSpace();

        List<Queue> queuesInWaitStatus = iQueueRepository.getQueuesInWaitStatus(availableQueueSpace);
        queuesInWaitStatus.forEach( queue -> {
            queue.toOngoing(QUEUE_EXPIRED_TIME);
            queueOption.incrementOngoingCount();
        });

        iQueueRepository.updateStatusToOngoing(
                queuesInWaitStatus.stream()
                        .map(QueueConverter::toEntity)
                        .toList()
        );
    }
}
