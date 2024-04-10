package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.api.concert.domain.queue.QueueOption.QUEUE_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class QueueServiceTest {

    private final QueueService queueService;
    private final QueueOption queueOption;
    private final IQueueRepository iQueueRepository;

    @Autowired
    public QueueServiceTest(QueueService queueService, QueueOption queueOption, IQueueRepository iQueueRepository) {
        this.queueService = queueService;
        this.queueOption = queueOption;
        this.iQueueRepository = iQueueRepository;
    }

    @Test
    void test() throws InterruptedException{
        //Given
        QueueRequest queueRequest = QueueRequest.builder().userId(1L).build();
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        //When
        int numberOfRequests = 50;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for(int i = 0; i < numberOfRequests; i++){
            Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    queueService.register(QueueRequest.builder().userId(userId).build());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //Then
        log.info("success count {} ", successCount);
        log.info("fail count {}", failCount);
        log.info("status ONGOING {}", iQueueRepository.getCountOfOngoingStatus());
        long countOfOngoingStatus = iQueueRepository.getCountOfOngoingStatus();
        assertThat(countOfOngoingStatus).isEqualTo(QUEUE_LIMIT);
    }


}
