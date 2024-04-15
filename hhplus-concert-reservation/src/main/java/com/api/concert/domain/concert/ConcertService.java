package com.api.concert.domain.concert;

import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConcertService {

    private final IConcertRepository iConcertRepository;
    private final IConcertOptionRepository iConcertOptionRepository;

    private final IConcertSeatRepository iConcertSeatRepository;

    public List<ConcertInfo> findAvailableConcerts() {
        return iConcertOptionRepository.availableConcerts();
    }

    public ConcertSeatResponse findAvailableConcertSeat(Long concertOptionId) {
        List<ConcertSeat> reservedSeats = iConcertSeatRepository.findReservedSeats(concertOptionId);
        return ConcertSeat.toResponse(
                concertOptionId,
                ConcertSeat.checkAvailableSeats(reservedSeats)
        );
    }
}
