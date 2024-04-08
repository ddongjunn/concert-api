package com.api.concert.domain.queue;

import com.api.concert.infrastructure.queue.QueueEntity;
import org.springframework.data.domain.Limit;

import java.time.LocalDateTime;
import java.util.List;

public interface IQueueRepository {
    long getCountOfOngoingStatus();

    Queue save(QueueEntity queueEntity);

    boolean existsByUserIdAndOngoingStatus(Long userId);

    List<Queue> getExpiredOngoingStatus(LocalDateTime queueExpiredTime);

    void updateStatusQueues(List<QueueEntity> updateStatusQueueList);

    List<Queue> getQueuesInWaitStatus(int limit);
}
