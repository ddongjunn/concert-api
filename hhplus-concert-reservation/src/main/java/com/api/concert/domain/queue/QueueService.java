package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.queue.projection.WaitingRank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final int QUEUE_LIMIT = 5;
    private final int QUEUE_EXPIRED_TIME = 1;
    private final IQueueRepository iQueueRepository;

    // 대기열 등록
    @Transactional
    public QueueRegisterResponse register(QueueRegisterRequest queueRegisterRequestDto){
        Long userId = queueRegisterRequestDto.getUserId();
        validateUserNotRegisteredOrThrow(userId);

        Queue queue = Queue.builder().userId(userId).build();
        assignQueueStatusByAvailability(queue);

        return QueueConverter.toRegisterResponse(
                iQueueRepository.save(QueueConverter.toEntity(queue))
        );
    }

    public void validateUserNotRegisteredOrThrow(Long userId) {
        Queue queue = iQueueRepository.findByUserIdAndStatusIn(userId, List.of(WaitingStatus.WAIT, WaitingStatus.ONGOING));
        if(queue == null){
            return;
        }

        queue.assertNotWait();
        queue.assertNotOngoing();
    }

    public void assignQueueStatusByAvailability(Queue queue) {
        //findByStatusWithPessimisticLock -> 비관적 락을 걸어도 동시성 의미 없는 상태
        List<Queue> ongoingStatus = iQueueRepository.findByStatusWithPessimisticLock(WaitingStatus.ONGOING);

        if (ongoingStatus.size() < QUEUE_LIMIT) {
            queue.toOngoing(QUEUE_EXPIRED_TIME);
        } else {
            queue.toWait();
        }
    }

    @Transactional
    @Scheduled(cron = "${queue.scan-time}")
    public void updateExpiredQueuesAndActivateNewOnes(){
        List<Queue> expiredOngoingStatus = iQueueRepository.findExpiredOngoingStatus(WaitingStatus.ONGOING, LocalDateTime.now());
        if(!expiredOngoingStatus.isEmpty()){
            expireQueues(expiredOngoingStatus);
            activateQueuesByExpireQueuesSize(expiredOngoingStatus.size());
        }
    }

    public void expireQueues(List<Queue> expiredQueues) {
        expiredQueues.forEach(Queue::expiry);

        iQueueRepository.updateStatusFromWaitToOngoingOrExpired(
                expiredQueues.stream()
                        .map(QueueConverter::toEntity)
                        .toList()
        );
    }

    public void activateQueuesByExpireQueuesSize(int availableQueueSpace) {
        List<Queue> queuesInWaitStatus = iQueueRepository.findQueuesInWaitStatus(WaitingStatus.WAIT, availableQueueSpace);
        queuesInWaitStatus.forEach( queue -> {
            queue.toOngoing(QUEUE_EXPIRED_TIME);
        });

        iQueueRepository.updateStatusFromWaitToOngoingOrExpired(
                queuesInWaitStatus.stream()
                        .map(QueueConverter::toEntity)
                        .toList()
        );
    }

    public QueueStatusResponse detail(Long concertWaitingId) {
        Queue queue = iQueueRepository.findById(concertWaitingId);
        validateQueueExists(queue);

        queue.assertNotOngoing();
        updateQueueByWaitingRank(queue, concertWaitingId);
        return QueueConverter.toStatusResponse(queue);
    }

    public void validateQueueExists(Queue queue) {
        if (queue == null) {
            throw new CommonException(ResponseCode.TICKET_NOT_ISSUED, ResponseCode.TICKET_NOT_ISSUED.getMessage());
        }
    }

    public void updateQueueByWaitingRank(Queue queue, Long concertWaitingId) {
        WaitingRank waitingRank = iQueueRepository.countWaitingAhead(concertWaitingId);
        queue.updateWaitingNumber(waitingRank.getRanking());
    }
}
