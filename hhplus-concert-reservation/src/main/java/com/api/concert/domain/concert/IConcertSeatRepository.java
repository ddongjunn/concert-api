package com.api.concert.domain.concert;

import com.api.concert.infrastructure.concert.ConcertSeatEntity;

import java.util.List;

public interface IConcertSeatRepository {
    List<ConcertSeat> findReservedSeats(Long concertOptionId);

    ConcertSeat findByConcertOptionIdAndSeatNo(Long concertOptionId, int seatNo);

    ConcertSeat save(ConcertSeatEntity concertSeatEntity);
}
