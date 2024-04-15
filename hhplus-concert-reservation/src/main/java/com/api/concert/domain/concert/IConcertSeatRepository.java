package com.api.concert.domain.concert;

import java.util.List;

public interface IConcertSeatRepository {
    List<ConcertSeat> findReservedSeats(Long concertOptionId);
}
