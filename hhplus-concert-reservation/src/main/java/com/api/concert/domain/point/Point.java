package com.api.concert.domain.point;

import com.api.concert.domain.point.constant.TransactionType;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.exception.InsufficientPointsException;
import com.api.concert.global.common.model.ResponseCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

@Getter
@NoArgsConstructor
public class Point {

    private Long pointId;
    private Long userId;
    private Long point = 0L;
    private TransactionType transactionType;

    @Builder
    public Point(Long pointId, Long userId, Long point, TransactionType transactionType){
        this.pointId = pointId;
        this.userId = userId;
        this.point = point;
        this.transactionType = transactionType;
    }

    public void charge(Long chargePoint, Consumer<PointHistory> saveHistory){
        if(chargePoint < 0){
            throw new CommonException(ResponseCode.POINT_CHARGE_NEGATIVE, ResponseCode.POINT_CHARGE_NEGATIVE.getMessage());
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
