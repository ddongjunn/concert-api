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

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RScoredSortedSet<String> waitQueue;
    private final RMapCache<String, String> ongoingQueue;

    @Override
    public int register(Long userId) {
        return waitQueue.addAndGetRank(System.currentTimeMillis(), userId.toString());
    }

    @Override
    public int getOngoingCount() {
        return ongoingQueue.size();
    }

    @Override
    public List<Long> pollUsersFromWaitQueue(int availableQueueCount) {
        return waitQueue.pollFirst(availableQueueCount)
                .stream()
                .mapToLong(Long::parseLong)
                .boxed()
                .toList();
    }

    @Override
    public void activate(List<Long> userIds, long ttl) {
        String expiryTime = LocalDateTime.now().plusSeconds(ttl).format(DATE_TIME_FORMATTER);
        log.info("[queue activate] userIds {}, expiryTime {}", userIds, expiryTime);
        userIds.forEach(userId -> {
                String strUserId = String.valueOf(userId);
                ongoingQueue.put(strUserId, expiryTime, ttl, TimeUnit.SECONDS);
        });
    }

    @Override
    public Optional<String> findUserExpiredTimeInOngoingQueue(Long userId) {
        String expiryTime = ongoingQueue.get(userId.toString());
        return Optional.ofNullable(expiryTime);
    }

    @Override
    public Optional<Integer> findUserRankInWaitQueue(Long userId) {
        return Optional.ofNullable(waitQueue.rank(userId.toString()));
    }

    @Override
    public void expireOngoingQueue(Long userId) {
        ongoingQueue.remove(userId.toString());
    }
}
