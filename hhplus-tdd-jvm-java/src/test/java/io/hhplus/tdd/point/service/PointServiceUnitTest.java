package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.database.PointHistoryTable;
import io.hhplus.tdd.point.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.domain.constant.TransactionType;
import io.hhplus.tdd.point.dto.PointDto;
import io.hhplus.tdd.point.dto.request.PointRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceUnitTest {

    @Mock
    UserPointTable userPointTable;

    @Mock
    PointHistoryTable pointHistoryTable;

    @InjectMocks
    PointService pointService;
    @Test
    public void 특정유저_포인트_조회() throws InterruptedException {
        // Given
        Long userId = 1L;
        Long amount = 0L;

        // When
        when(userPointTable.selectById(anyLong())).thenReturn(UserPoint.empty(userId));

        // Then
        UserPoint userPoint = pointService.checkPoint(userId);
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(amount);
    }

    @Test
    public void 포인트를_음수로_충전하는경우_예외() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, -500L);

        // Then
        assertThatThrownBy(() -> pointService.chargePoint(pointDto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void 포인트_충전시_포인트테이블_금액확인() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, 500L);

        // When
        when(userPointTable.selectById(anyLong())).thenReturn(new UserPoint(pointDto.userId(), 0L, System.currentTimeMillis()));
        when(userPointTable.insertOrUpdate(anyLong(), anyLong())).thenReturn(new UserPoint(pointDto.userId(), pointDto.amount(), eq(System.currentTimeMillis())));

        // Then
        UserPoint userPoint = pointService.chargePoint(pointDto);
        assertThat(userPoint.point()).isEqualTo(500L);
    }

    @Test
    public void 포인트_사용시_포인트확인() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, 500L);

        // When
        when(userPointTable.selectById(anyLong())).thenReturn(new UserPoint(pointDto.userId(), 0L, System.currentTimeMillis()));
        when(userPointTable.insertOrUpdate(anyLong(), anyLong())).thenReturn(new UserPoint(pointDto.userId(), pointDto.amount(), eq(System.currentTimeMillis())));

        // Then
        UserPoint userPoint = pointService.chargePoint(pointDto);
        assertThat(userPoint.point()).isEqualTo(500L);
    }

    @Test
    public void 포인트충전_내역_조회() throws Exception {
        // Given
        Long userId = 1L;

        // When
        List<PointHistory> pointHistories = pointService.checkPointHistory(userId);

        // Then
        assertThat(pointHistories).hasSize(0);
        assertThat(pointHistories).isEmpty();
    }
    @Test
    public void 포인트충전_내역_조회2() throws Exception {
        // Given
        List<PointHistory> pointHistories = pointService.checkPointHistory(1L);
        pointHistories.add(PointHistory.Empty(1L));
        pointHistories.add(PointHistory.Empty(1L));

        // Then
        assertThat(pointHistories).hasSize(2);
    }

    private PointDto getPointDto(Long userId, Long amount){
        PointRequest pointRequest = new PointRequest(amount);
        return pointRequest.toDto(userId, amount);
    }
}

