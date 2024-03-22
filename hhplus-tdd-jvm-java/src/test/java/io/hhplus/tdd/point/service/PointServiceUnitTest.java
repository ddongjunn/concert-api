package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.database.PointHistoryTable;
import io.hhplus.tdd.point.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.domain.constant.TransactionType;
import io.hhplus.tdd.point.dto.PointDto;
import io.hhplus.tdd.point.dto.request.PointRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
        given(pointService.checkPoint(userId)).willReturn(UserPoint.empty(userId));

        // When
        UserPoint userPoint = pointService.checkPoint(userId);

        //Then
        assertThat(userPoint.id()).isEqualTo(UserPoint.empty(userId).id());
        assertThat(userPoint.point()).isEqualTo(UserPoint.empty(userId).point());
    }

    @Test
    public void 포인트를_음수로_충전하는경우_예외() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, -500L);

        // Then
        assertThatThrownBy(() -> pointService.chargePoint(pointDto))
                .isInstanceOf(RuntimeException.class);
    }


    private PointDto getPointDto(Long userId, Long amount){
        PointRequest pointRequest = new PointRequest(amount);
        return pointRequest.toDto(userId, amount);
    }
}

