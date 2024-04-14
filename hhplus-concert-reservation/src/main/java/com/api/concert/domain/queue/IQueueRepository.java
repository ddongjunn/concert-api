package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.infrastructure.queue.QueueEntity;
import com.api.concert.infrastructure.queue.projection.WaitingRank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IQueueRepository {
    long getCountOfOngoingStatus();

    Queue save(QueueEntity queueEntity);

    boolean existsByUserIdAndStatusIsOngoingOrWaiting(Long userId);

    List<Queue> findExpiredOngoingStatus(WaitingStatus status, LocalDateTime now);

    void updateStatusFromWaitToOngoingOrExpired(List<QueueEntity> entities);

    List<Queue> findQueuesInWaitStatus(WaitingStatus status, int limit);

    Queue findById(Long concertWaitingId);

    WaitingRank countWaitingAhead(Long concertWaitingId);

    List<Queue> findOngoingStatus(WaitingStatus status);

    Queue findByUserIdAndStatusIn(Long userId, List<WaitingStatus> asList);
}
