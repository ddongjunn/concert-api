package com.api.concert.controller.concert.dto;

import lombok.Builder;

@Builder
public record ConcertTempReservationRequest(Long userId, Long concertOptionId, int seatNo) {
}
