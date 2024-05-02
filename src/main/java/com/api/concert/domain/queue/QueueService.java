package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.global.common.annotation.DistributedLock;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.queue.projection.WaitingRank;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final IQueueRedisRepository iQueueRedisRepository;

    // 대기열 등록
    @Transactional
    public QueueRegisterResponse register(QueueRegisterRequest queueRegisterRequest){
        Long userId = queueRegisterRequest.getUserId();
        int rank = iQueueRedisRepository.register(userId);
        return QueueRegisterResponse.builder()
                .rank(rank)
                .build();
    }

    public QueueStatusResponse detail(Long userId) {
        String ttl = iQueueRedisRepository.findExpirationTimeForUser(userId)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_EXIST_WAITING_USER, ResponseCode.NOT_EXIST_WAITING_USER.getMessage()));

        return QueueStatusResponse.builder()
                .expiredTime(ttl)
                .build();
    }
}
