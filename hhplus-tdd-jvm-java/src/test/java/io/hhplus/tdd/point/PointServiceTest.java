package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PointServiceTest {

    public PointService pointService;

    @Autowired
    public PointServiceTest(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @Test
    @DisplayName("[포인트 충전] 포인트가 존재 하지 않는 사용자의 포인트 충전")
    public void GivenUserId_WhenChargingPoint_ThenUserPoint() throws InterruptedException {
        // Given
        Long userId = 1L;
        Long amount = 500L;

        // When
        UserPoint userPoint = pointService.pointCharge(userId, amount);

        // Then
        UserPoint result = new UserPoint(1L, 500L, System.currentTimeMillis());
        assertThat(userPoint).isEqualTo(result);
    }

    @Test
    @DisplayName("[포인트 추가 충전] 포인트가 이미 존재 하는 사용자의 포인트 충전 ")
    public void GivenUserId_WhenRechargingPoint_ThenUserPoint() throws InterruptedException {
        // Given
        Long userId = 1L;
        Long amount = 500L;

        // When
        UserPoint rechargeUserPoint = pointService.pointCharge(userId, amount);

        // Then
        UserPoint result = new UserPoint(userId, 1000L, rechargeUserPoint.updateMillis());
        assertThat(rechargeUserPoint).isEqualTo(result);
    }
}
