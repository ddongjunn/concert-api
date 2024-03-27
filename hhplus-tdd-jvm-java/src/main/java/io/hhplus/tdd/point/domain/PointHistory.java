package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.domain.constant.TransactionType;

public record PointHistory(
        Long id,
        Long userId,
        TransactionType type,
        Long amount,
        Long timeMillis
) {

    public static PointHistory Empty(long id){
        return new PointHistory(1L, 0L, TransactionType.USE, 0L, System.currentTimeMillis());
    }
}
