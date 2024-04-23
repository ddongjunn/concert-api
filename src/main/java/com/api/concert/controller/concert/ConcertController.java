package com.api.concert.controller.concert;

import com.api.concert.application.ConcertFacade;
import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.controller.concert.dto.ConcertTempReservationRequest;
import com.api.concert.controller.concert.dto.ConcertTempReservationResponse;
import com.api.concert.infrastructure.concert.projection.ConcertInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ConcertController {

    private final ConcertFacade concertFacade;
    @GetMapping("/concert/reservation/dates")
    public List<ConcertInfoProjection> concerts(){
        return concertFacade.retrieveAvailableConcerts();
    }

    @GetMapping("/concert/{concertOptionId}/reservation/seats")
    public ConcertSeatResponse seatsForReservationList(@PathVariable Long concertOptionId){
        return concertFacade.retrieveAvailableSeats(concertOptionId);
    }

    @PostMapping("/concert/reservation")
    public ConcertTempReservationResponse concertReservation(@RequestBody ConcertTempReservationRequest request){
        return concertFacade.temporaryReservationSeat(request);
    }
}
