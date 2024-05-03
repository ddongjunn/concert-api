package com.api.concert.domain.queue;

import java.util.List;
import java.util.Optional;

public interface IQueueRedisRepository {
    int register(Long userId);

    int getOngoingCount();

    List<Long> getUserToActivate(int ongoingCount);

    void activate(List<Long> userIds, int queueExpiredTime);

    Optional<String> findExpirationTimeForUser(Long userId);
}
