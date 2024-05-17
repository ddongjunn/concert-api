package com.api.concert.controller.mockapi;

import com.api.concert.domain.concert.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MockApiController {

    @PostMapping("/api/data")
    public String externalMockApi(@RequestBody List<Reservation> reservations){
        log.info("externalMockApi {}", reservations.toString());
        return "SUCCESS";
    }
}
