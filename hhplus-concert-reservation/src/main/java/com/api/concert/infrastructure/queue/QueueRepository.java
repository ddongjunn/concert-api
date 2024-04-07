package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.IQueueRepository;
import com.api.concert.domain.queue.Queue;
import com.api.concert.domain.queue.QueueConverter;
import com.api.concert.domain.queue.constant.WaitingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueueRepository implements IQueueRepository {
    private final QueueJpaRepository queueJpaRepository;

    @Override
    public long getOngoingCountStatusCount() {
        return queueJpaRepository.countByStatus(WaitingStatus.ONGOING);
    }

    @Override
    public Queue save(QueueEntity queueEntity) {
        return QueueConverter.toDomain(
                queueJpaRepository.save(queueEntity)
        );
    }

    @Override
    public boolean existsByUserIdAndOngoingStatus(Long userId) {
        return queueJpaRepository.existsByUserIdAndStatus(userId, WaitingStatus.ONGOING);
    }
}
