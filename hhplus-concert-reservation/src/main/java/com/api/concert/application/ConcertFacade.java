package com.api.concert.application;

import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.controller.concert.dto.ConcertTempReservationRequest;
import com.api.concert.controller.concert.dto.ConcertTempReservationResponse;
import com.api.concert.domain.concert.ConcertSeatService;
import com.api.concert.domain.concert.ConcertService;
import com.api.concert.infrastructure.concert.projection.ConcertInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ConcertFacade {
    private final ConcertService concertService;
    private final ConcertSeatService concertSeatService;

    public List<ConcertInfoProjection> retrieveAvailableConcerts() {
        return concertService.findAvailableConcerts();
    }

    public ConcertSeatResponse retrieveAvailableSeats(Long concertOptionId) {
        return concertSeatService.findAvailableSeatsForConcert(concertOptionId);
    }

    public ConcertTempReservationResponse temporaryReservationSeat(ConcertTempReservationRequest request){
        concertService.checkConcertOptionId(request.concertOptionId());
        return concertSeatService.temporaryReservationSeat(request);
    }

}
