package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.infrastructure.queue.QueueEntity;
import com.api.concert.infrastructure.queue.QueueJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.api.concert.domain.queue.QueueOption.QUEUE_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class QueueServiceTest {

    private final QueueService queueService;
    private final QueueOption queueOption;
    private final IQueueRepository iQueueRepository;

    private final QueueJpaRepository queueJpaRepository;

    @Autowired
    public QueueServiceTest(QueueService queueService, QueueOption queueOption, IQueueRepository iQueueRepository, QueueJpaRepository queueJpaRepository) {
        this.queueService = queueService;
        this.queueOption = queueOption;
        this.iQueueRepository = iQueueRepository;
        this.queueJpaRepository = queueJpaRepository;
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
        long countOfOngoingStatus = iQueueRepository.getCountOfOngoingStatus();
        List<QueueEntity> all = queueJpaRepository.findAll();
        all.forEach(a -> log.info("list : {}", a.toString()));
        assertThat(countOfOngoingStatus).isEqualTo(1L);
    }

    @DisplayName("동시에 여러명이 대기열 등록")
    @Test
    void test_register() throws InterruptedException{
        //Given
        QueueRegisterRequest queueRegisterRequest = QueueRegisterRequest.builder().userId(1L).build();

        //When
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
        long countOfOngoingStatus = iQueueRepository.getCountOfOngoingStatus();
        assertThat(countOfOngoingStatus).isEqualTo(QUEUE_LIMIT);
    }

}
