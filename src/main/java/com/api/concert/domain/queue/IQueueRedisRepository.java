package com.api.concert.domain.queue;

import java.util.List;
import java.util.Optional;

public interface IQueueRedisRepository {
    int register(Long userId);

    int getOngoingCount();

    List<Long> pollUsersFromWaitQueue(int availableQueueCount);

    void activate(List<Long> userIds, long ttl);

    Optional<String> findUserExpiredTimeInOngoingQueue(Long userId);

    Optional<Integer> findUserRankInWaitQueue(Long userId);

    void expireOngoingQueue(Long userId);
}
