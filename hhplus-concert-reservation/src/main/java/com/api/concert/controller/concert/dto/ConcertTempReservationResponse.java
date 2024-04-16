package com.api.concert.controller.concert.dto;

import com.api.concert.global.common.model.ResponseCode;
import lombok.Builder;

@Builder
public record ConcertTempReservationResponse(ResponseCode code, String message) {
}
