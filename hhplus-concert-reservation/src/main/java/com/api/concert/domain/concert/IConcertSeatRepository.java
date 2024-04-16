package com.api.concert.domain.concert;

import com.api.concert.domain.concert.constant.SeatStatus;
import com.api.concert.infrastructure.concert.ConcertSeatEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface IConcertSeatRepository {
    List<ConcertSeat> findReservedSeats(Long concertOptionId);

    ConcertSeat findByConcertOptionIdAndSeatNo(Long concertOptionId, int seatNo);

    void save(ConcertSeatEntity concertSeatEntity);

    List<ConcertSeat> findExpiredTemporarySeats(SeatStatus status, LocalDateTime minusMinutes);
}
