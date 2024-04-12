package com.api.concert.application;

import com.api.concert.domain.concert.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConcertFacade {
    private final ConcertService concertService;

    public String getAvailableConcerts(){
        concertService.findAvailableConcerts();
        return "";
    }
}
