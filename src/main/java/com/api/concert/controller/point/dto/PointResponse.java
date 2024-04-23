package com.api.concert.controller.point.dto;

import com.api.concert.global.common.model.ResponseCode;
import lombok.Builder;

@Builder
public record PointResponse (ResponseCode responseCode, Long point){
}
