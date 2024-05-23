package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.common.exception.CommonException;
import com.api.concert.common.model.ResponseCode;
import com.api.concert.infrastructure.queue.QueueJpaRepository;
import config.TestContainerSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCache;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.api.concert.domain.queue.scheduled.QueueScheduledService.QUEUE_EXPIRED_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class QueueServiceIntegrationTest extends TestContainerSupport {

    private final QueueService queueService;
    private final RScoredSortedSet<String> waitQueue;
    private final RMapCache<String, String> ongoingQueue;

    @Autowired
    public QueueServiceIntegrationTest(QueueService queueService, IQueueRepository iQueueRepository, QueueJpaRepository queueJpaRepository, RScoredSortedSet<String> waitQueue, RedissonClient redissonClient, RMapCache<String, String> ongoingQueue) {
        this.queueService = queueService;
        this.waitQueue = waitQueue;
        this.ongoingQueue = ongoingQueue;
    }

    @BeforeEach
    void setUp(){
        waitQueue.clear();
        ongoingQueue.clear();
    }

    @DisplayName("대기열 등록하는 경우 순번 반환")
    @Test
    void test_register(){
        // Given
        QueueRegisterRequest queueRegisterRequest = QueueRegisterRequest.builder().userId(1L).build();

        // When
        QueueRegisterResponse result = queueService.register(queueRegisterRequest);

        // Then
        assertThat(result.getRank()).isEqualTo(1);
    }

    @DisplayName("대기열을 재등록하면 맨 뒤에 순번으로 이동")
    @Test
    void test_register_re(){
        // Given
        for(int i = 1; i <= 5; i++){
            QueueRegisterRequest queueRegisterRequest = QueueRegisterRequest.builder().userId((long) i).build();
            queueService.register(queueRegisterRequest);
        }

        QueueRegisterRequest queueRegisterRequest = QueueRegisterRequest.builder().userId(1L).build();

        // When
        QueueRegisterResponse result = queueService.register(queueRegisterRequest);

        // Then
        assertThat(result.getRank()).isEqualTo(5);
    }

    @DisplayName("대기열 상태 확인시 대기열 만료시간 반환")
    @Test
    void test_getQueueStatus(){
        // Given
        Long userId = 1L;
        String expiryTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().plusSeconds(QUEUE_EXPIRED_TIME));
        ongoingQueue.put(String.valueOf(userId), expiryTime, QUEUE_EXPIRED_TIME, TimeUnit.SECONDS);

        // When
        QueueStatusResponse result = queueService.getQueueStatus(userId);

        // Then
        assertThat(result.getExpiredTime()).isEqualTo(expiryTime);
    }

    @DisplayName("대기열 조회 시 대기열에 존재하지 않는 경우 Exception")
    @Test
    void test_getQueueStatus_not_exists(){
        // Given
        Long userId = 1L;

        // When & Then
        assertThatThrownBy(() -> queueService.getQueueStatus(userId))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.NOT_EXIST_WAITING_USER)
                .hasMessage(ResponseCode.NOT_EXIST_WAITING_USER.getMessage());
    }

    @DisplayName("한명의 사용자가 동시에 신청하는 경우")
    @Test
    void test_register_onlyOne() throws InterruptedException{
        //Given
        QueueRegisterRequest queueRegisterRequest = QueueRegisterRequest.builder().userId(1L).build();

        //When
        int numberOfRequests = 10;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for(int i = 0; i < numberOfRequests; i++){
            executorService.submit(() -> {
                try {
                    queueService.register(queueRegisterRequest);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //Then
        long countOfOngoingStatus = waitQueue.size();
        assertThat(countOfOngoingStatus).isEqualTo(1L);
    }

    @DisplayName("동시에 여러명이 대기열 등록")
    @Test
    void test_register_concurrency() throws InterruptedException{
        //Given & When
        int numberOfRequests = 100;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for(int i = 0; i < numberOfRequests; i++){
            Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    queueService.register(QueueRegisterRequest.builder().userId(userId).build());
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //Then
        long result = waitQueue.size();
        assertThat(result).isEqualTo(numberOfRequests);
    }
}
