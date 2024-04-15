package com.api.concert.controller.concert.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ConcertSeatResponse {
    private Long concertOptionId;
    private List<ConcertSeatDTO> seats;
}
