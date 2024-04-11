package com.api.concert.domain.point;

import com.api.concert.domain.point.constant.TransactionType;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Point {
    private Long userId;
    private Long point = 0L;
    private TransactionType transactionType;

    @Builder
    public Point(Long userId, Long point, TransactionType transactionType){
        this.userId = userId;
        this.point = point;
        this. transactionType = transactionType;
    }

    public void charge(Long chargePoint){
        if(chargePoint < 0){
            throw new CommonException(ResponseCode.POINT_CHARGE_NEGATIVE, ResponseCode.POINT_CHARGE_NEGATIVE.getMessage());
        }

        this.point += chargePoint;
        this.transactionType = TransactionType.CHARGE;
    }

    public void use(Long usePoint) {
        this.point -= usePoint;
        this.transactionType = TransactionType.USE;

        if(this.point < 0){
            throw new CommonException(ResponseCode.NOT_ENOUGH_POINT, ResponseCode.NOT_ENOUGH_POINT.getMessage());
        }
    }
}
