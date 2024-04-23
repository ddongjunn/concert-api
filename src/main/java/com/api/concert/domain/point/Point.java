package com.api.concert.domain.point;

import com.api.concert.domain.point.constant.TransactionType;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.domain.point.exception.InsufficientPointsException;
import com.api.concert.global.common.model.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {

    private Long pointId;
    private Long userId;
    private Long point;
    private TransactionType transactionType;

    public void charge(Long chargePoint, Consumer<PointHistory> saveHistory){
        if(chargePoint < 0){
            throw new CommonException(ResponseCode.POINT_CHARGE_NEGATIVE, ResponseCode.POINT_CHARGE_NEGATIVE.getMessage());
        }

        if(this.point == null){
            this.point = 0L;
        }

        this.point += chargePoint;
        this.transactionType = TransactionType.CHARGE;

        saveHistory.accept(PointHistory.builder()
                .userId(this.userId)
                .point(chargePoint)
                .type(this.transactionType)
                .build()
        );
    }

    public void use(Long usePoint, Consumer<PointHistory> saveHistory) {
        Long currentPoint = this.point;
        this.point -= usePoint;
        this.transactionType = TransactionType.USE;

        if(this.point < 0){
            long insufficientAmount = -this.point;
            throw new InsufficientPointsException(
                    ResponseCode.NOT_ENOUGH_POINT.getMessage(),
                    ResponseCode.NOT_ENOUGH_POINT,
                    insufficientAmount,
                    currentPoint
            );
        }

        saveHistory.accept(PointHistory.builder()
                .userId(this.userId)
                .point(usePoint)
                .type(this.transactionType)
                .build()
        );
    }
}
