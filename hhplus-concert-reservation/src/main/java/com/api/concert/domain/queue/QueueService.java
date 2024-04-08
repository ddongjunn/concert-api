package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRequest;
import com.api.concert.controller.queue.dto.QueueResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.exception.AlreadyWaitingUserException;
import com.api.concert.infrastructure.queue.QueueEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        if(iQueueRepository.existsByUserIdAndOngoingStatus(userId)){
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
        log.info("updateStatusToDone start");
        // 만료시간(QUEUE_EXPIRED_TIME)이 지난 ONGOING -> DONE update
        List<Queue> expiredOngoingStatus = iQueueRepository.getExpiredOngoingStatus(LocalDateTime.now());
        updateStatusToDone(expiredOngoingStatus);
        expiredOngoingStatus.forEach( q ->
                log.info("expiredOngoingStatus : {}", q.toString())
        );

        //log.info("WAIT -> ONGOING start");
        //updateStatusToOngoingForWaitQueues();
        //log.info("WAIT -> ONGOING end");
    }

    public void updateStatusToDone(List<Queue> queues) {
        queues.forEach(Queue::updateStatusToDone);
        updateStatusQueues(queues);
    }

    public void updateStatusToOngoingForWaitQueues() {
        long currentOngoingCount = iQueueRepository.getCountOfOngoingStatus();
        int availableQueueSpace = (int) (QUEUE_LIMIT - currentOngoingCount);

        List<Queue> queuesInWaitStatus = iQueueRepository.getQueuesInWaitStatus(availableQueueSpace);
        queuesInWaitStatus.forEach(queue -> {
            queue.updateStatusToOngoing();
            queue.updateQueueExpiredAt();
            log.info("queuesInWaitStatus : {}",queue.toString());
        });

        updateStatusQueues(queuesInWaitStatus);
    }

    public void updateStatusQueues(List<Queue> queues) {
        iQueueRepository.updateStatusQueues(queues.stream().map(QueueConverter::toEntity).toList());
    }

}
