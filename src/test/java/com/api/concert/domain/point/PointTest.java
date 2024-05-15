package com.api.concert.domain.point;

import com.api.concert.domain.point.constant.TransactionType;
import com.api.concert.common.exception.CommonException;
import com.api.concert.domain.point.exception.InsufficientPointsException;
import com.api.concert.common.model.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PointTest {

    private Point point;

    @BeforeEach
    void setUp(){
        this.point = new Point();
    }

    @DisplayName("[포인트 충전] - 포인트가 없는 경우")
    @Test
    void test_charge(){
        // Given
        Long chargePoint = 1000L;

        // When
        point.charge(chargePoint, pointHistory -> {

        });

        // Then
        assertThat(point.getPoint()).isEqualTo(1000L);
        assertThat(point.getTransactionType()).isEqualTo(TransactionType.CHARGE);
    }

    @DisplayName("[포인트 충전] - 포인트가 있는 경우")
    @Test
    void test_charge_hasPoint(){
        // Given
        Point point = Point.builder().userId(1L).point(500L).transactionType(TransactionType.CHARGE).build();
        Long chargePoint = 1000L;

        // When
        point.charge(chargePoint, pointHistory -> {

        });

        // Then
        assertThat(point.getPoint()).isEqualTo(1500L);
        assertThat(point.getTransactionType()).isEqualTo(TransactionType.CHARGE);
    }


    @DisplayName("[포인트 충전] - 음수 충전")
    @Test
    void test_charge_negative(){
        // Given
        Point point = Point.builder().userId(1L).point(500L).build();
        Long chargePoint = -1000L;

        // When & Then
        assertThatThrownBy(() -> point.charge(chargePoint, pointHistory -> {

        }))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.POINT_CHARGE_NEGATIVE)
                .hasMessage(ResponseCode.POINT_CHARGE_NEGATIVE.getMessage());
    }

    @DisplayName("[포인트 사용]")
    @Test
    void test_use(){
        //Given
        Point point = Point.builder().userId(1L).point(1000L).build();
        Long usePoint = 500L;

        //When
        point.use(usePoint, pointHistory -> {

        });

        //Then
        assertThat(point.getPoint()).isEqualTo(500L);
        assertThat(point.getTransactionType()).isEqualTo(TransactionType.USE);
    }

    @DisplayName("[포인트 사용] - 포인트가 부족한 경우")
    @Test
    void test_use_notEnough(){
        //Given
        Point point = Point.builder().userId(1L).point(1000L).build();
        Long usePoint = 1500L;

        //When & Then
        assertThatThrownBy(() -> point.use(usePoint, pointHistory -> {}))
                .isInstanceOf(InsufficientPointsException.class)
                .hasFieldOrPropertyWithValue("code", ResponseCode.NOT_ENOUGH_POINT)
                .hasFieldOrPropertyWithValue("insufficientPoint", 500L)
                .hasFieldOrPropertyWithValue("currentPoint", 1000L)
                .hasMessage(ResponseCode.NOT_ENOUGH_POINT.getMessage());
    }


}