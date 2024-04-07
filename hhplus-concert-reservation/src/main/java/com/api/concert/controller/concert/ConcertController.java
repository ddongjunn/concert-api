package com.api.concert.controller.concert;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConcertController {

    @GetMapping("/concert/reservation/dates")
    public String concertsForReservationList(){
        return """
                {
                    "concerts" [
                        {
                            "concert_id": 1,
                            "concert_name": "다나카 내한 공연［SAYONARA... TANAKA",
                            "concert_singer": "다나카",
                            "concert_venue": "서강대 메리홀 대극장",
                            "concert_start_date": "2024-04-26 20:00:00"
                        },
                        {
                            "concert_id": 2,
                            "concert_name": "2024 성시경의 축가 콘서트",
                            "concert_singer": "성시경",
                            "concert_venue": "연세대학교 노천극장",
                            "concert_start_date": "2024-09-10 19:30:00"
                        },
                        {
                             "concert_id": 3,
                             "concert_name": "황영웅 대전콘서트 〈봄날의 고백>",
                             "concert_singer": "황영웅",
                             "concert_venue": "대전 컨벤션센터 제2전시장",
                             "concert_start_date": "2024-05-25 17:00:00"
                        }
                    ]
                }
                """;
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
