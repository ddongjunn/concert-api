package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.infrastructure.queue.QueueEntity;
import com.api.concert.infrastructure.queue.projection.WaitingRank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IQueueRepository {
    Queue save(QueueEntity queueEntity);

    List<Queue> findExpiredOngoingStatus(WaitingStatus status, LocalDateTime now);

    void updateStatusFromWaitToOngoingOrExpired(List<QueueEntity> entities);

    List<Queue> findQueuesInWaitStatus(WaitingStatus status, int limit);

    Queue findById(Long concertWaitingId);

    WaitingRank countWaitingAhead(Long concertWaitingId);

    List<Queue> findByStatusWithPessimisticLock(WaitingStatus status);

    Queue findByUserIdAndStatusIn(Long userId, List<WaitingStatus> asList);

}
