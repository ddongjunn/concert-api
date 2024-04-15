package com.api.concert.controller.concert;

import com.api.concert.application.ConcertFacade;
import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ConcertController {

    private final ConcertFacade concertFacade;
    @GetMapping("/concert/reservation/dates")
    public List<ConcertInfo> concerts(){
        return concertFacade.retrieveAvailableConcerts();
    }

    //TODO 예약 불가능한 콘서트에 대한 예외처리
    @GetMapping("/concert/{concertOptionId}/reservation/seats")
    public ConcertSeatResponse seatsForReservationList(@PathVariable Long concertOptionId){
        return concertFacade.retrieveAvailableSeats(concertOptionId);
    }

    @PostMapping("/concert/reservation")
    public String concertReservation(){
        return """
                {
                  "code": "SUCCESS",
                  "message": "좌석 임시 배정 완료, 만료시간 : yyyy-mm-dd hh:mm:ss"
                }
                """;
    }
}
