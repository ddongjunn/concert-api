package com.api.concert.controller.point.dto;

import lombok.Builder;

@Builder
public record PointUseRequest(Long userId, Long point) {
}
