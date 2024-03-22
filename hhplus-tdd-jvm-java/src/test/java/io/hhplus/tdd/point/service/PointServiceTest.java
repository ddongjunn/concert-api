package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.database.PointHistoryTable;
import io.hhplus.tdd.point.database.UserPointTable;
import io.hhplus.tdd.point.dto.PointDto;
import io.hhplus.tdd.point.dto.request.PointRequest;
import io.hhplus.tdd.point.error.ErrorCode;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.constant.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PointServiceTest {

    public PointService pointService;

    @Autowired
    public PointServiceTest(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @BeforeEach
    void setUp() {
        pointService = new PointService(new UserPointTable(), new PointHistoryTable());
    }

    @Test
    @DisplayName("[포인트 충전] - 포인트가 없는 경우")
    public void GivenUserIdAndAmount_WhenChargingPoint_ThenUserPoint() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, 500L);

        // When
        UserPoint userPoint = pointService.chargePoint(pointDto);

        // Then
        UserPoint result = pointService.checkPoint(pointDto.userId());
        assertThat(userPoint.id()).isEqualTo(result.id());
        assertThat(userPoint.point()).isEqualTo(result.point());
    }

    @Test
    @DisplayName("[포인트 충전] - 포인트를 음수로 충전하는 경우")
    public void GivenUserIdAndNegativeAmount_WhenChargingPoint_ThenException() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, -500L);

        // Then
        assertThatThrownBy(() -> pointService.chargePoint(pointDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ErrorCode.INCORRECT_AMOUNT.getMessage());

    }

    @Test
    @DisplayName("[포인트 추가 충전] - 포인트가 있는 경우")
    public void GivenUserIdAndAmount_WhenRechargingPoint_ThenUserPoint() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, 500L);

        pointService.chargePoint(pointDto);

        // When
        UserPoint rechargeUserPoint = pointService.chargePoint(pointDto);

        // Then
        UserPoint result = pointService.checkPoint(pointDto.userId());
        assertThat(rechargeUserPoint.id()).isEqualTo(result.id());
        assertThat(rechargeUserPoint.point()).isEqualTo(result.point());
    }

    @Test
    @DisplayName("[포인트 조회] - 포인트가 있는 경우")
    public void GivenUserId_WhenCheckingPoint_ThenUserPoint() throws Exception {
        //Given
        PointDto pointDto = getPointDto(1L, 1000L);
        UserPoint userPoint = pointService.chargePoint(pointDto);

        // When
        UserPoint result = pointService.checkPoint(pointDto.userId());

        // Then
        assertThat(userPoint.id()).isEqualTo(result.id());
        assertThat(userPoint.point()).isEqualTo(result.point());
    }

    @Test
    @DisplayName("[포인트 조회] - 포인트가 없는 경우")
    public void GivenUserId_WhenCheckingPoint_ThenNoPointExist() throws InterruptedException {
        //Given
        Long userId = 1L;
        Long amount = 0L;
        UserPoint userPoint = new UserPoint(userId, amount, System.currentTimeMillis());

        // When
        UserPoint result = pointService.checkPoint(userId);

        // Then
        assertThat(userPoint.id()).isEqualTo(result.id());
        assertThat(userPoint.point()).isEqualTo(result.point());
    }

    @Test
    @DisplayName("[포인트 사용] - 잔고가 부족한 경우")
    public void GivenUserIdAndAmount_WhenUsingPoint_ThenException() throws Exception {
        //Given
        Long userId = 1L;
        Long amount = 100L;
        PointDto pointDto = getPointDto(userId, amount);

        assertThatThrownBy(() -> pointService.usePoint(pointDto))
                .isInstanceOf(Exception.class)
                .hasMessage(ErrorCode.INCORRECT_AMOUNT.getMessage(pointDto.amount()));
    }

    @Test
    @DisplayName("[포인트 사용] - 포인트가 있는 경우")
    public void GivenUserIdAndAmount_WhenUsingPoint_ThenRemainingPoint() throws Exception {
        // Given
        PointDto chargePointDto = getPointDto(1L, 1000L);
        UserPoint userPoint = pointService.chargePoint(chargePointDto);

        // When
        PointDto usePointDto = getPointDto(1L, 400L);
        UserPoint result = pointService.usePoint(usePointDto);

        // Then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(600L);
    }

    @Test
    @DisplayName("[포인트 사용 내역 조회] - 포인트가 있는 경우")
    public void GivenUserIdAndAmount_WhenUsingPointHistories_ThenPointHistory() throws Exception {
        // Given
        PointDto chargePointDto = getPointDto(1L, 1000L);
        UserPoint userPoint = pointService.chargePoint(chargePointDto);

        PointDto usePointDto = getPointDto(1L, 400L);
        UserPoint result = pointService.usePoint(usePointDto);

        // When
        PointHistory pointHistory = pointService.checkPointHistory(1L)
                .stream()
                .filter(history -> history.type().equals(TransactionType.USE))
                .findFirst()
                .orElse(null);

        // Then
        assertThat(pointHistory.userId()).isEqualTo(1L);
        assertThat(pointHistory.amount()).isEqualTo(400L);
        assertThat(pointHistory.type()).isEqualTo(TransactionType.USE);
        assertThat(result.point()).isEqualTo(600L);
    }

    @Test
    @DisplayName("[포인트 충전 내역 조회] - 포인트 충전 내역이 있는 경우")
    public void GivenUserId_WhenCheckingPointHistories_ThenNoPointHistoriesExist() throws Exception {
        //Given
        PointDto pointDto = getPointDto(1L, 1000L);
        UserPoint userPoint = pointService.chargePoint(pointDto);


        // When
        PointHistory pointHistory = pointService.checkPointHistory(pointDto.userId())
                .stream()
                .filter(history -> history.type().equals(TransactionType.CHARGE))
                .findFirst()
                .orElse(null);

        // Then
        assertThat(pointHistory.userId()).isEqualTo(userPoint.id());
        assertThat(pointHistory.amount()).isEqualTo(userPoint.point());
        assertThat(pointHistory.type()).isEqualTo(TransactionType.CHARGE);
    }

    @Test
    @DisplayName("[포인트 충전 내역 조회] - 포인트 충전 내역이 없는 경우")
    public void GivenUserId_WhenCheckingPointHistories_ThenPointHistoriesExist() throws InterruptedException {
        //Given
        Long userId = 1L;

        // When
        List<PointHistory> pointHistories = pointService.checkPointHistory(userId);

        // Then
        assertThat(pointHistories).isEmpty();
    }

    @Test
    @DisplayName("[포인트 사용 동시성 테스트]")
    public void givenAmount_whenConcurrentPointUse_thenPointZero() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, 1000L);
        Long[] usageAmounts = {300L, 700L};
        UserPoint userPoint = pointService.chargePoint(pointDto);

        int threadsNum = 2;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(threadsNum);

        for (int i = 0; i < threadsNum; i++) {
            long usageAmount = usageAmounts[i];
            executorService.submit(() -> {
                try {
                    pointService.usePoint(pointDto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        assertThat(pointService.checkPoint(pointDto.userId()).point()).isEqualTo(0L);
        assertThat(successCount.intValue() + failCount.intValue()).isEqualTo(threadsNum);
    }

    @Test
    @DisplayName("[포인트 충전 동시성 테스트]")
    public void givenUserPoint_whenConcurrentCharging_thenPoint() throws InterruptedException {
        // Given
        PointDto pointDto = getPointDto(1L, 100L);

        int threadNum = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(threadNum);

        // When
        for(int i = 0; i < threadNum; i++){
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(pointDto);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        assertThat(pointService.checkPoint(pointDto.userId()).point()).isEqualTo(1000L);
    }

    private PointDto getPointDto(Long userId, Long amount){
        PointRequest pointRequest = new PointRequest(amount);
        return pointRequest.toDto(userId, amount);
    }


}
