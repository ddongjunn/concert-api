package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.IQueueRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RScoredSortedSet;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class QueueRedisRepository implements IQueueRedisRepository {

    private final RScoredSortedSet<Long> waitQueue;
    private final RMapCache<Long, Long> ongoingQueue;

    @Override
    public int register(Long userId) {
        waitQueue.add(System.currentTimeMillis(), userId);
        return waitQueue.rank(userId);
    }

    @Override
    public int getOngoingCount() {
        return ongoingQueue.size();
    }

    @Override
    public List<Long> pollUsersFromWaitQueue(int availableQueueCount) {
        return waitQueue.pollFirst(availableQueueCount)
                .stream()
                .toList();
    }

    @Override
    public void activate(List<Long> userIds, int ttl) {
        long deadTime = Instant.now().plusSeconds(ttl).getEpochSecond();
        userIds.forEach(userId -> {
                ongoingQueue.put(userId, deadTime, ttl, TimeUnit.SECONDS);
        });
    }

    @Override
    public Optional<Long> findUserExpiredTimeInOngoingQueue(Long userId) {
        return Optional.ofNullable(ongoingQueue.get(userId));
    }

    @Override
    public Optional<Integer> findUserRankInWaitQueue(Long userId) {
        return Optional.ofNullable(waitQueue.rank(userId));
    }

    @Override
    public void expireOngoingQueue(Long userId) {
        ongoingQueue.remove(userId);
    }
}
