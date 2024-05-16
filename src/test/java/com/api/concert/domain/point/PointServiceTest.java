package com.api.concert.domain.point;

import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.controller.point.dto.PointChargeResponse;
import com.api.concert.controller.point.dto.PointUseRequest;
import com.api.concert.common.exception.CommonException;
import com.api.concert.domain.point.exception.InsufficientPointsException;
import com.api.concert.common.model.ResponseCode;
import com.api.concert.infrastructure.point.PointEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    IPointRepository iPointRepository;

    @Mock
    IPointHistoryRepository iPointHistoryRepository;

    @InjectMocks
    PointService pointService;

    @DisplayName("포인트 조회")
    @Test
    void test_findPoint(){
        // Given
        Long userId = 1L;
        Point point = Point.builder().userId(userId).point(500L).build();

        // When
        when(iPointRepository.findPointByUserId(anyLong())).thenReturn(point);
        Point result = pointService.getPoint(userId);

        // Then
        assertThat(result.getUserId()).isEqualTo(point.getUserId());
        assertThat(result.getPoint()).isEqualTo(point.getPoint());
    }

    @DisplayName("[포인트 충전] - 정상")
    @Test
    void test_charge(){
        // Given
        PointChargeRequest pointChargeRequest = createPointChargeRequest(1L, 500L);
        Long userId = pointChargeRequest.userId();
        Long chargePoint = pointChargeRequest.point();

        // When
        Point findPoint = Point.builder().userId(userId).point(1000L).build();
        when(iPointRepository.findPointByUserId(anyLong())).thenReturn(findPoint);

        Point pointCharge = Point.builder().userId(userId).point(findPoint.getPoint() + chargePoint).build();
        when(iPointRepository.updatePoint(any(PointEntity.class))).thenReturn(pointCharge);

        // Then
        PointChargeResponse result = pointService.charge(pointChargeRequest);
        assertThat(result.chargePoint()).isEqualTo(500L);
        assertThat(result.point()).isEqualTo(1500L);
    }

    @DisplayName("[포인트 충전] - 음수")
    @Test
    void test_charge_negative(){
        // Given
        PointChargeRequest pointChargeRequest = createPointChargeRequest(1L, -500L);
        Long userId = pointChargeRequest.userId();
        Long chargePoint = pointChargeRequest.point();

        // When
        Point findPoint = Point.builder().userId(userId).point(1000L).build();
        when(iPointRepository.findPointByUserId(anyLong())).thenReturn(findPoint);

        // Then
        assertThatThrownBy(() -> pointService.charge(pointChargeRequest))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.POINT_CHARGE_NEGATIVE)
                .hasMessage(ResponseCode.POINT_CHARGE_NEGATIVE.getMessage());
    }

    @DisplayName("[포인트 사용] - 포인트가 부족할 경우")
    @Test
    void test_use_lack(){
        // Given
        Long userId = 1L;
        Long usePoint = 1000L;
        PointUseRequest pointUseRequest = createPointUseRequest(userId, usePoint);

        // When
        Point point = Point.builder().pointId(1L).userId(1L).point(0L).build();
        when(iPointRepository.findPointByUserId(userId)).thenReturn(point);

        // Then
        assertThatThrownBy(() -> pointService.use(pointUseRequest))
                .isInstanceOf(InsufficientPointsException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.NOT_ENOUGH_POINT)
                .hasFieldOrPropertyWithValue("insufficientPoint", 1000L)
                .hasFieldOrPropertyWithValue("currentPoint", 0L)
                .hasMessage(ResponseCode.NOT_ENOUGH_POINT.getMessage());
    }

    @DisplayName("[포인트 사용] - 정상")
    @Test
    void test_use(){
        // Given
        Long userId = 1L;
        Long usePoint = 1000L;
        PointUseRequest pointUseRequest = createPointUseRequest(userId, usePoint);

        // When
        Point findPoint = Point.builder().pointId(1L).userId(1L).point(2000L).build();
        when(iPointRepository.findPointByUserId(userId)).thenReturn(findPoint);

        Point updatePoint = Point.builder().pointId(1L).userId(1L).point(1000L).build();
        when(iPointRepository.updatePoint(any(PointEntity.class))).thenReturn(updatePoint);

        PointChargeResponse result = pointService.use(pointUseRequest);

        // Then
        verify(iPointRepository, times(1)).updatePoint(any(PointEntity.class));
        assertThat(result.point()).isEqualTo(1000);
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