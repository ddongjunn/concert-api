package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.infrastructure.queue.QueueEntity;
import com.api.concert.infrastructure.queue.QueueJpaRepository;
import com.api.concert.util.DataClearExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(DataClearExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class QueueServiceConcurrencyTest {

    private final int QUEUE_LIMIT = 5;
    private final int QUEUE_EXPIRED_TIME = 1;

    private final QueueService queueService;
    private final IQueueRepository iQueueRepository;

    private final QueueJpaRepository queueJpaRepository;

    @Autowired
    public QueueServiceConcurrencyTest(QueueService queueService, IQueueRepository iQueueRepository, QueueJpaRepository queueJpaRepository) {
        this.queueService = queueService;
        this.iQueueRepository = iQueueRepository;
        this.queueJpaRepository = queueJpaRepository;
    }

    /***
     * user_id, status -> unique key 생성하여 해결
     * 기존 DONE 상태 제거 isExpired 필드로 만료상태 구분
     */
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
        long countOfOngoingStatus = queueJpaRepository.countByStatus(WaitingStatus.ONGOING);
        List<QueueEntity> all = queueJpaRepository.findAll();
        assertThat(countOfOngoingStatus).isEqualTo(1L);
    }

    /**
     * TODO
     * 동시성 테스트 OngoingCount를 관리하는 별도의 테이블을 두어야 할지 고민..
     */
    @DisplayName("동시에 여러명이 대기열 등록")
    @Test
    void test_register() throws InterruptedException{
        //Given
        QueueRegisterRequest queueRegisterRequest = QueueRegisterRequest.builder().userId(1L).build();

        //When
        int numberOfRequests = 10;
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
        long countOfOngoingStatus = queueJpaRepository.countByStatus(WaitingStatus.ONGOING);
        List<QueueEntity> all = queueJpaRepository.findAll();
        all.forEach( queueEntity ->
                log.info("{}",queueEntity.toString())
        );
        assertThat(countOfOngoingStatus).isEqualTo(QUEUE_LIMIT);
    }

}
