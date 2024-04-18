package com.api.concert.domain.point;

import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.infrastructure.point.PointEntity;
import com.api.concert.infrastructure.point.PointJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class PointConcurrencyTest {

    private final PointService pointService;
    private final IPointRepository iPointRepository;
    private final PointJpaRepository pointJpaRepository;

    @Autowired
    public PointConcurrencyTest(PointService pointService, IPointRepository iPointRepository, PointJpaRepository pointJpaRepository) {
        this.pointService = pointService;
        this.iPointRepository = iPointRepository;
        this.pointJpaRepository = pointJpaRepository;
    }

    @DisplayName("한명의 사용자가 포인트를 동시에 충전 하는 경우")
    @Test
    void test() throws InterruptedException {
        proxyCheck();

        //Given
        PointChargeRequest pointChargeRequest = createPointChargeRequest(1L, 500L);

        //When
        int numberOfRequests = 10;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for(int i = 0; i < numberOfRequests; i++){
            executorService.submit(() -> {
                try {
                    pointService.charge(pointChargeRequest);
                } catch (Exception e) {
                    log.error("exception {}",e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //Then

        List<PointEntity> all = pointJpaRepository.findAll();
        all.forEach(p -> log.info("point = {}", p.getPoint()));

        Point result = iPointRepository.findPointByUserId(1L);
        assertThat(result.getPoint()).isEqualTo(5500L);
    }

    @Test
    void proxyCheck() {
        //proxy 체크
        log.info("aop class={}", pointService.getClass());
        assertThat(AopUtils.isAopProxy(pointService)).isTrue();
    }

    PointChargeRequest createPointChargeRequest(Long userId, Long point){
        return PointChargeRequest.builder()
                .userId(userId)
                .point(point)
                .build();
    }
}
