package com.api.concert.domain.queue;

import com.api.concert.infrastructure.queue.QueueEntity;
import com.api.concert.infrastructure.queue.projection.WaitingRank;

import java.util.List;

public interface IQueueRepository {
    long getCountOfOngoingStatus();

    Queue save(QueueEntity queueEntity);

    boolean existsByUserIdAndStatusIsOngoingOrWaiting(Long userId);

    List<Queue> getExpiredOngoingStatus();

    void updateStatusToDone(List<Long> updateIds);

    void updateStatusToOngoing(List<QueueEntity> entities);

    List<Queue> getQueuesInWaitStatus(int limit);

    Queue findById(Long concertWaitingId);

    WaitingRank countWaitingAhead(Long concertWaitingId);
}
