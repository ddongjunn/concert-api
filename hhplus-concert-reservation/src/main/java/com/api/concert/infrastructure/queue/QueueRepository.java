package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.IQueueRepository;
import com.api.concert.domain.queue.Queue;
import com.api.concert.domain.queue.QueueConverter;
import com.api.concert.domain.queue.constant.WaitingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public boolean existsByUserIdAndOngoingStatus(Long userId) {
        return queueJpaRepository.existsByUserIdAndStatus(userId, WaitingStatus.ONGOING);
    }

    @Override
    public List<Queue> getExpiredOngoingStatus(LocalDateTime queueExpiredTime) {
        return queueJpaRepository.findByStatusAndExpiredAtBefore(WaitingStatus.ONGOING, queueExpiredTime)
                .stream()
                .map(QueueConverter::toDomain)
                .toList();
    }

    @Override
    public void updateStatusQueues(List<QueueEntity> entities) {
        entities.forEach(queueEntity -> {
                    log.info("queueEntity id : {}, status : {}",queueEntity.getConcertWaitingId(), queueEntity.getStatus());
                    queueJpaRepository.updateStatusByConcertWaitingId(queueEntity.getStatus(), queueEntity.getConcertWaitingId());
                });
    }

    @Override
    public List<Queue> getQueuesInWaitStatus(int limit) {
        return queueJpaRepository.findWaitStatusOrderByCreatedAt(PageRequest.of(0, limit))
                .stream()
                .map(QueueConverter::toDomain)
                .toList();
    }
}
