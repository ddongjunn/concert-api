package com.api.concert.controller.concert;

import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.controller.concert.dto.ConcertTempReservationRequest;
import com.api.concert.controller.concert.dto.ConcertTempReservationResponse;
import com.api.concert.domain.concert.ConcertSeatService;
import com.api.concert.domain.concert.ConcertService;
import com.api.concert.infrastructure.concert.projection.ConcertInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ConcertController {

    private final ConcertService concertService;
    private final ConcertSeatService concertSeatService;
    @GetMapping("/concert/reservation/dates")
    public List<ConcertInfoProjection> concerts(){
        return concertService.findAvailableConcerts();
    }

    @GetMapping("/concert/{concertOptionId}/reservation/seats")
    public ConcertSeatResponse seatsForReservationList(@PathVariable Long concertOptionId){
        return concertSeatService.findAvailableSeatsForConcert(concertOptionId);
    }

    @PostMapping("/concert/reservation")
    public ConcertTempReservationResponse concertReservation(@RequestBody ConcertTempReservationRequest request){
        return concertSeatService.temporaryReservationSeat(request);
    }
}
