package com.api.concert.controller.concert.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ConcertSeatResponse (Long concertOptionId, List<ConcertSeatDTO> seats) {
}
