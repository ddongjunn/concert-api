package com.api.concert.controller.concert;

import com.api.concert.application.ConcertFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ConcertController {

    private final ConcertFacade concertFacade;
    @GetMapping("/concert/reservation/dates")
    public String concertsForReservationList(){
        return concertFacade.getAvailableConcerts();
    }

    @GetMapping("/concert/{concertId}/reservation/seats")
    public String seatsForReservationList(@PathVariable Long concertId){
        return """
                {
                  "seats": [
                    {
                      "seat_id": 1,
                      "seat_number": 1,
                      "price": 5000
                    },
                    {
                      "seat_id": 23,
                      "seat_number": 16,
                      "price": 5000
                    },
                    {
                      "seat_id": 29,
                      "seat_number": 17,
                      "price": 5000
                    },
                    {
                      "seat_id": 59,
                      "seat_number": 25,
                      "price": 7500
                    },
                    {
                      "seat_id": 61,
                      "seat_number": 48,
                      "price": 10000
                    },
                    {
                      "seat_id": 89,
                      "seat_number": 50,
                      "price": 10000
                    }
                  ]
                }
                """;
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
