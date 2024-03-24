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

    @Test
    //질문사항
    public void 테스트() throws Exception {
        // Given
        PointDto pointDto = getPointDto(1L, 500L);

        // (1)
        //given(userPointTable.selectById(anyLong())).willReturn(new UserPoint(pointDto.userId(), 0L, System.currentTimeMillis()).charge(500L));
        //given(userPointTable.insertOrUpdate(anyLong(), anyLong())).willReturn(new UserPoint(pointDto.userId(), pointDto.amount(), System.currentTimeMillis()));

        // (2)
        // 실제 서비스 코드에서 .charge(pointDto.amount()); 이럴때 테스트를 어떻게 해야할지...
        // 강제로 결과값에 추가하게 되면 inserOr
        given(userPointTable.selectById(1L)).willReturn(new UserPoint(pointDto.userId(), 0L, System.currentTimeMillis()));

        given(userPointTable.insertOrUpdate(1L, 500L)).willReturn(new UserPoint(pointDto.userId(), pointDto.amount(), eq(System.currentTimeMillis())));

        UserPoint userPoint = pointService.chargePoint(pointDto);

        assertThat(userPoint.point()).isEqualTo(500L);
    }


    private PointDto getPointDto(Long userId, Long amount){
        PointRequest pointRequest = new PointRequest(amount);
        return pointRequest.toDto(userId, amount);
    }
}

