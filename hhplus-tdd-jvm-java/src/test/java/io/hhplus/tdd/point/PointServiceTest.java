package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
    public void GivenUserIdAndAmount_WhenChargingPoint_ThenUserPoint() throws InterruptedException {
        // Given
        Long userId = 1L;
        Long amount = 500L;

        // When
        UserPoint userPoint = pointService.chargePoint(userId, amount);

        // Then
        UserPoint result = pointService.checkPoint(userId);
        assertThat(userPoint.id()).isEqualTo(result.id());
        assertThat(userPoint.point()).isEqualTo(result.point());
    }

    @Test
    @DisplayName("[포인트 추가 충전] - 포인트가 있는 경우")
    public void GivenUserIdAndAmount_WhenRechargingPoint_ThenUserPoint() throws InterruptedException {
        // Given
        Long userId = 1L;
        Long amount = 500L;
        pointService.chargePoint(userId, amount);

        // When
        UserPoint rechargeUserPoint = pointService.chargePoint(userId, 500L);

        // Then
        UserPoint result = new UserPoint(userId, 1000L, rechargeUserPoint.updateMillis());
        assertThat(rechargeUserPoint.id()).isEqualTo(result.id());
        assertThat(rechargeUserPoint.point()).isEqualTo(result.point());
    }

    @Test
    @DisplayName("[포인트 조회] - 포인트가 있는 경우")
    public void GivenUserId_WhenCheckingPoint_ThenUserPoint() throws InterruptedException {
        //Given
        Long userId = 1L;
        Long amount = 1000L;
        UserPoint userPoint = pointService.chargePoint(userId, amount);

        // When
        UserPoint result = pointService.checkPoint(userId);

        // Then
        assertThat(userPoint.id()).isEqualTo(result.id());
        assertThat(userPoint.point()).isEqualTo(result.point());
    }

    @Test
    @DisplayName("[포인트 조회] - 포인트가 없는 경우")
    public void GivenUserId_WhenCheckingPoint_ThenNoPointExist() throws InterruptedException {
        //Given
        Long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 0L, System.currentTimeMillis());

        // When
        UserPoint result = pointService.checkPoint(userId);

        // Then
        assertThat(userPoint.id()).isEqualTo(result.id());
        assertThat(userPoint.point()).isEqualTo(result.point());
    }

    @Test
    @DisplayName("[포인트 충전 내역 조회] - 포인트 충전 내역이 있는 경우")
    public void GivenUserId_WhenCheckingPointHistories_ThenNoPointHistoriesExist() throws InterruptedException {
        //Given
        Long userId = 1L;
        Long amount = 1000L;
        pointService.chargePoint(userId, amount);

        // When
        List<PointHistory> pointHistories = pointService.checkPointHistory(userId);

        // Then
        PointHistory result = new PointHistory(1L, userId, TransactionType.CHARGE, 1000L, System.currentTimeMillis());
        assertThat(pointHistories).hasSize(1);

        PointHistory firstPointHistory = pointHistories.get(0);
        assertThat(firstPointHistory.id()).isEqualTo(result.id());
        assertThat(firstPointHistory.userId()).isEqualTo(result.userId());
        assertThat(firstPointHistory.type()).isEqualTo(result.type());
        assertThat(firstPointHistory.amount()).isEqualTo(result.amount());
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
    @DisplayName("[포인트 사용] - 잔고가 부족한 경우")
    public void GivenUserIdAndAmount_WhenUsingPoint_ThenException() throws Exception {
        //Given
        Long userId = 1L;
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(1L, 0L, System.currentTimeMillis());

        assertThatThrownBy(() -> pointService.usePoint(userId, amount))
                .isInstanceOf(Exception.class)
                .hasMessage("사용할 수 있는 포인트가 부족합니다. 현재 포인트 : " + userPoint.point());
    }

    @Test
    @DisplayName("[포인트 사용] - 포인트가 있는 경우")
    public void GivenUserIdAndAmount_WhenUsingPoint_ThenRemainingPoint() throws Exception {
        // Given
        Long userId = 1L;
        Long chargePoint = 1000L;
        UserPoint userPoint = pointService.chargePoint(userId, chargePoint);

        // When
        Long usePoint = 400L;
        UserPoint result = pointService.usePoint(userId, usePoint);

        // Then
        assertThat(userPoint.id()).isEqualTo(result.id());
        assertThat(chargePoint - usePoint).isEqualTo(result.point());
    }
}
