package com.api.concert.controller.point.dto;

import lombok.Builder;
import lombok.Data;

@Builder
public record PointChargeRequest (Long userId, Long point) {
}
