package com.api.concert.domain.point;

import com.api.concert.modules.event.Events;
import com.api.concert.domain.point.constant.TransactionType;
import com.api.concert.common.exception.CommonException;
import com.api.concert.domain.point.event.PointHistoryLoggedEvent;
import com.api.concert.domain.point.exception.InsufficientPointsException;
import com.api.concert.common.model.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {

    private Long pointId;
    private Long userId;
    private Long point;
    private TransactionType transactionType;

    public void charge(Long chargePoint){
        if(chargePoint < 0){
            throw new CommonException(ResponseCode.POINT_CHARGE_NEGATIVE, ResponseCode.POINT_CHARGE_NEGATIVE.getMessage());
        }

        if(this.point == null){
            this.point = 0L;
        }

        this.point += chargePoint;
        this.transactionType = TransactionType.CHARGE;
        saveHistory();
    }

    public void use(Long usePoint) {
        if(this.point == null){
            this.point = 0L;
        }

        if(this.point < usePoint) {
            long insufficientAmount = usePoint - this.point;
            throw new InsufficientPointsException(
                    ResponseCode.NOT_ENOUGH_POINT.getMessage(),
                    ResponseCode.NOT_ENOUGH_POINT,
                    insufficientAmount,
                    this.point
            );
        }

        this.point -= usePoint;
        this.transactionType = TransactionType.USE;
        saveHistory();
    }

    public void saveHistory() {
        Events.raise(
                PointHistoryLoggedEvent.builder()
                        .userId(this.userId)
                        .point(this.point)
                        .type(this.transactionType)
                        .build()
        );
    }
}
