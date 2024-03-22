package io.hhplus.tdd.point.dto;

public record PointDto(Long userId, Long amount) {

    public static PointDto of(Long userId, Long amount){
        return new PointDto(userId, amount);
    }
}
