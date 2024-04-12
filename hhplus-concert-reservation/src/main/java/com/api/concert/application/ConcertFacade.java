package com.api.concert.application;

import com.api.concert.controller.concert.dto.ConcertResponseDto;
import com.api.concert.domain.concert.ConcertService;
import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ConcertFacade {
    private final ConcertService concertService;

    public List<ConcertInfo> getConcerts() {
        return concertService.getAvailableForReservationConcerts();
    }
}
