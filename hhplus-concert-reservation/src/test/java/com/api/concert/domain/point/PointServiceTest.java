package com.api.concert.domain.point;

import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.controller.point.dto.PointChargeResponse;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    IPointRepository iPointRepository;

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
        Point result = pointService.findPointForService(userId);

        // Then
        assertThat(result.getUserId()).isEqualTo(point.getUserId());
        assertThat(result.getPoint()).isEqualTo(point.getPoint());
    }

    @DisplayName("포인트 충전")
    @Test
    void test_charge(){
        // Given
        PointChargeRequest pointChargeRequest = createPointRequest(1L, 500L);
        Long userId = pointChargeRequest.userId();
        Long chargePoint = pointChargeRequest.point();

        // When
        Point findPoint = Point.builder().userId(userId).point(1000L).build();
        when(iPointRepository.findPointByUserId(anyLong())).thenReturn(findPoint);

        Point pointCharge = Point.builder().userId(userId).point(findPoint.getPoint() + chargePoint).build();
        when(iPointRepository.updatePoint(any(PointEntity.class))).thenReturn(pointCharge);
        findPoint.charge(chargePoint);

        // Then
        PointChargeResponse result = pointService.charge(pointChargeRequest);
        assertThat(result.chargePoint()).isEqualTo(500L);
        assertThat(result.point()).isEqualTo(1500L);
    }

    @DisplayName("포인트 음수 충전")
    @Test
    void test_charge_negative(){
        // Given
        PointChargeRequest pointChargeRequest = createPointRequest(1L, -500L);
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

    PointChargeRequest createPointRequest(Long userId, Long point){
        return PointChargeRequest.builder()
                .userId(userId)
                .point(point)
                .build();
    }

}