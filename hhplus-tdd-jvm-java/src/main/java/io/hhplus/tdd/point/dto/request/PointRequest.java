package io.hhplus.tdd.point.dto.request;

import io.hhplus.tdd.point.dto.PointDto;

public record PointRequest(
        Long amount
) {

    public PointDto toDto(Long userId, Long amount){
        return PointDto.of(
                userId,
                amount
        );
    }
}
