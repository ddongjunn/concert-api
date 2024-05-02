package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.IQueueRepository;
import com.api.concert.domain.queue.Queue;
import com.api.concert.domain.queue.QueueConverter;
import com.api.concert.domain.queue.constant.WaitingStatus;
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
    public Queue save(QueueEntity queueEntity) {
        return QueueConverter.toDomain(
                queueJpaRepository.save(queueEntity)
        );
    }

    @Override
    public List<Queue> findExpiredOngoingStatus(WaitingStatus status, LocalDateTime now) {
        return queueJpaRepository.findByStatusAndExpiredAtIsBefore(status, now)
                .stream()
                .map(QueueConverter::toDomain)
                .toList();
    }

    @Override
    public void updateStatusFromWaitToOngoingOrExpired(List<QueueEntity> entities) {
        entities.forEach(queueEntity -> {
            queueJpaRepository.updateStatusAndExpiredAtById(queueEntity.getStatus(), queueEntity.isExpired(), queueEntity.getExpiredAt(), queueEntity.getQueueId());
        });
    }

    @Override
    public List<Queue> findQueuesInWaitStatus(WaitingStatus status, int limit) {
        return queueJpaRepository.findWaitStatusOrderByCreatedAt(status, PageRequest.of(0, limit))
                .stream()
                .map(QueueConverter::toDomain)
                .toList();
    }

    @Override
    public Queue findById(Long concertWaitingId) {
        return queueJpaRepository.findById(concertWaitingId)
                .map(QueueConverter::toDomain)
                .orElse(null);
    }

    @Override
    public WaitingRank countWaitingAhead(Long concertWaitingId) {
        return queueJpaRepository.findWaitingRankById(concertWaitingId,"WAIT");
    }

    @Override
    public List<Queue> findByStatusWithPessimisticLock(WaitingStatus status) {
        return queueJpaRepository.findByStatusWithPessimisticLock(status)
                .stream()
                .map(QueueConverter::toDomain)
                .toList();
    }

    @Override
    public Queue findByUserIdAndStatusIn(Long userId, List<WaitingStatus> asList) {
        return queueJpaRepository.findByUserIdAndStatusIn(userId, asList)
                .map(QueueConverter::toDomain)
                .orElse(null);
    }

    @Override
    public Long findOngoingCount() {
        return queueJpaRepository.countByStatus(WaitingStatus.ONGOING);
    }
}
