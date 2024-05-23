package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.common.exception.CommonException;
import com.api.concert.common.model.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final IQueueRedisRepository iQueueRedisRepository;

    @Transactional
    public QueueRegisterResponse register(QueueRegisterRequest queueRegisterRequest){
        Long userId = queueRegisterRequest.getUserId();
        int rank = iQueueRedisRepository.register(userId);
        return QueueRegisterResponse.builder()
                .rank(rank + 1)
                .build();
    }

    @Transactional(readOnly = true)
    public QueueStatusResponse getQueueStatus(Long userId) {
        Optional<Integer> rank = iQueueRedisRepository.findUserRankInWaitQueue(userId);
        if(rank.isPresent()){
            return QueueStatusResponse.builder()
                    .rank(rank.get() + 1)
                    .build();
        }

        String ttl = iQueueRedisRepository.findUserExpiredTimeInOngoingQueue(userId)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_EXIST_WAITING_USER, ResponseCode.NOT_EXIST_WAITING_USER.getMessage()));

        return QueueStatusResponse.builder()
                .expiredTime(ttl)
                .build();
    }

    public void expire(Long userId) {
        iQueueRedisRepository.expireOngoingQueue(userId);
    }
}
