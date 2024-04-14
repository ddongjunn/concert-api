package com.api.concert.domain.concert;

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

    public List<ConcertInfo> getAvailableForReservationConcerts() {
        return iConcertRepository.availableConcerts();
    }
}
