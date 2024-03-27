package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.error.ErrorCode;

public record UserPoint(
        Long id,
        Long point,
        Long updateMillis
) {
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0L, System.currentTimeMillis());
    }

    public static UserPoint of(Long id, Long point, Long updateMillis){
        return new UserPoint(id, point, updateMillis);
    }

    public static UserPoint of(Long id, Long point){
        return UserPoint.of(id, point, System.currentTimeMillis());
    }

    public UserPoint charge(Long amount) {
        if(amount < 0){
            throw new RuntimeException(ErrorCode.INCORRECT_AMOUNT.getMessage());
        }

        return new UserPoint(id, point + amount, System.currentTimeMillis());
    }

    public UserPoint use(Long amount) throws Exception {
        if(point - amount < 0){
            throw new RuntimeException(ErrorCode.INCORRECT_AMOUNT.getMessage(point));
        }

        return new UserPoint(id, point - amount, System.currentTimeMillis());
    }
}
