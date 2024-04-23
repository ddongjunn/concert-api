package com.api.concert.controller.concert.dto;

import com.api.concert.global.common.model.ResponseCode;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
public record ConcertSeatResponse (Long concertOptionId, List<ConcertSeatDTO> seats) {
}
