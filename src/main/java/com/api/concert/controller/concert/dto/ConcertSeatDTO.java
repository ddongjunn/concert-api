package com.api.concert.controller.concert.dto;

import lombok.Builder;
import lombok.Data;

@Builder
public record ConcertSeatDTO(int seatNo, int price) {
}
