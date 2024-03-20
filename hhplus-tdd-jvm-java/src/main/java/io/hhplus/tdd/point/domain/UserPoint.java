package io.hhplus.tdd.point.domain;

public record UserPoint(
        Long id,
        Long point,
        Long updateMillis
) {
    public static UserPoint empty(long id) {
        return new UserPoint(id, 0L, System.currentTimeMillis());
    }
}
