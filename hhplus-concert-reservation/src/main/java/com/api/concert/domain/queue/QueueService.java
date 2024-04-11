package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.exception.AlreadyWaitingUserException;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.queue.projection.WaitingRank;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
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
    public QueueRegisterResponse register(QueueRegisterRequest queueRegisterRequestDto){
        Long userId = queueRegisterRequestDto.getUserId();
        isUserAlreadyRegistered(userId); //대기열에 WAIT, ONGOING 상태인 userId 존재 확인

        Queue queue = Queue.builder().userId(userId).build();
        assignQueueStatus(queue);

        return QueueConverter.toRegisterResponse(
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

    public QueueStatusResponse detail(Long concertWaitingId) {
        Queue queue = iQueueRepository.findById(concertWaitingId);

        if(queue.getStatus() == WaitingStatus.ONGOING){
            String message = String.format("대기열 만료 시간 [%s]", queue.getExpiredAt());
            throw new CommonException(ResponseCode.ALREADY_ONGOING_USER, message);
        }

        if(queue.getStatus() == WaitingStatus.WAIT){
            WaitingRank waitingRank = iQueueRepository.countWaitingAhead(concertWaitingId);
            queue.waitingNumber(waitingRank.getRanking());
            return QueueConverter.toStatusResponse(queue);
        }

        return QueueConverter.toStatusResponse(
                iQueueRepository.findById(concertWaitingId)
        );
    }
}
