package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.IQueueRepository;
import com.api.concert.domain.queue.Queue;
import com.api.concert.domain.queue.QueueConverter;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.queue.projection.WaitingRank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QueueRepository implements IQueueRepository {
    private final QueueJpaRepository queueJpaRepository;

    @Override
    public long getCountOfOngoingStatus() {
        return queueJpaRepository.countByStatus(WaitingStatus.ONGOING);
    }

    @Override
    public Queue save(QueueEntity queueEntity) {
        return QueueConverter.toDomain(
                queueJpaRepository.save(queueEntity)
        );
    }

    @Override
    public boolean existsByUserIdAndStatusIsOngoingOrWaiting(Long userId) {
        return queueJpaRepository.existsByUserIdAndStatusIn(userId, List.of(WaitingStatus.WAIT, WaitingStatus.ONGOING));
    }

    @Override
    public List<Queue> getExpiredOngoingStatus() {
        return queueJpaRepository.findByStatusAndExpiredAtIsBefore(WaitingStatus.ONGOING, LocalDateTime.now())
                .stream()
                .map(QueueConverter::toDomain)
                .toList();
    }

    @Override
    public void updateStatusToDone(List<Long> updateIds) {
        queueJpaRepository.updateStatusByConcertWaitingIds(WaitingStatus.DONE, updateIds);
    }

    @Override
    public void updateStatusToOngoing(List<QueueEntity> entities) {
        entities.forEach(queueEntity -> {
            queueJpaRepository.updateStatusAndExpiredAtByConcertWaitingId(WaitingStatus.ONGOING, queueEntity.getExpiredAt(), queueEntity.getConcertWaitingId());
        });
    }

    @Override
    public List<Queue> getQueuesInWaitStatus(int limit) {
        return queueJpaRepository.findWaitStatusOrderByCreatedAt(WaitingStatus.WAIT, PageRequest.of(0, limit))
                .stream()
                .map(QueueConverter::toDomain)
                .toList();
    }

    @Override
    public Queue findById(Long concertWaitingId) {
        return QueueConverter.toDomain(
                queueJpaRepository.findById(concertWaitingId)
                .orElseThrow(() -> new CommonException(ResponseCode.TICKET_NOT_ISSUED, ResponseCode.TICKET_NOT_ISSUED.getMessage()))
        );
    }

    @Override
    public WaitingRank countWaitingAhead(Long concertWaitingId) {
        return queueJpaRepository.findWaitingRankById(concertWaitingId,"WAIT");
    }
}
