package com.api.concert.domain.queue;

import com.api.concert.infrastructure.queue.QueueEntity;
import org.springframework.data.domain.Limit;

import java.time.LocalDateTime;
import java.util.List;

public interface IQueueRepository {
    long getCountOfOngoingStatus();

    Queue save(QueueEntity queueEntity);

    boolean existsByUserIdAndStatusIsOngoingOrWaiting(Long userId);

    List<Queue> getExpiredOngoingStatus();

    void updateStatusToDone(List<Long> updateIds);

    void updateStatusToOngoing(List<QueueEntity> entities);

    List<Queue> getQueuesInWaitStatus(int limit);
}
