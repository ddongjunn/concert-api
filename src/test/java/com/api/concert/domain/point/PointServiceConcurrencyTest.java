package com.api.concert.domain.point;

import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.controller.point.dto.PointUseRequest;
import com.api.concert.infrastructure.point.PointEntity;
import com.api.concert.infrastructure.point.PointHistoryEntity;
import com.api.concert.infrastructure.point.PointHistoryJpaRepository;
import com.api.concert.infrastructure.point.PointJpaRepository;
import com.api.concert.util.DataClearExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(DataClearExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PointServiceConcurrencyTest {

    private final PointService pointService;
    private final IPointRepository iPointRepository;
    private final PointJpaRepository pointJpaRepository;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    public PointServiceConcurrencyTest(PointService pointService, IPointRepository iPointRepository, PointJpaRepository pointJpaRepository, PointHistoryJpaRepository pointHistoryJpaRepository) {
        this.pointService = pointService;
        this.iPointRepository = iPointRepository;
        this.pointJpaRepository = pointJpaRepository;
        this.pointHistoryJpaRepository = pointHistoryJpaRepository;
    }

    @DisplayName("포인트를 동시에 충전 하는 경우 모두 충전되어야 한다.")
    @Test
    void test_charge() throws InterruptedException {
        //Given
        PointChargeRequest initPointChargeRequest = createPointChargeRequest(1L, 0L);
        pointService.charge(initPointChargeRequest);

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
                    log.error("exception {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //Then
        PointEntity result = pointJpaRepository.findByUserId(1L);
        assertThat(result.getPoint()).isEqualTo(5000L);

        List<PointHistoryEntity> histories = pointHistoryJpaRepository.findAll();
        assertThat(histories).hasSize(11);
    }

    @DisplayName("포인트를 동시에 사용 하는 경우 모두 사용되어야 한다.")
    @Test
    void test_use() throws InterruptedException {
        //Given
        PointChargeRequest initPointChargeRequest = createPointChargeRequest(1L, 5000L);
        pointService.charge(initPointChargeRequest);

        PointUseRequest pointUseRequest = createPointUseRequest(1L, 500L);

        //When
        int numberOfRequests = 10;
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        for(int i = 0; i < numberOfRequests; i++){
            executorService.submit(() -> {
                try {
                    pointService.use(pointUseRequest);
                } catch (Exception e) {
                    log.error("exception {}",e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        //Then
        PointEntity result = pointJpaRepository.findByUserId(1L);
        assertThat(result.getPoint()).isEqualTo(0L);

        List<PointHistoryEntity> histories = pointHistoryJpaRepository.findAll();
        assertThat(histories).hasSize(11);
    }

    PointChargeRequest createPointChargeRequest(Long userId, Long point){
        return PointChargeRequest.builder()
                .userId(userId)
                .point(point)
                .build();
    }

    PointUseRequest createPointUseRequest(Long userId, Long point){
        return PointUseRequest.builder()
                .userId(userId)
                .point(point)
                .build();
    }
}
