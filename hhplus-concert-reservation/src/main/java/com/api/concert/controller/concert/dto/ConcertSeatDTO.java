package com.api.concert.controller.concert.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConcertSeatDTO {
    private int seatNo;
    private int price;
}
