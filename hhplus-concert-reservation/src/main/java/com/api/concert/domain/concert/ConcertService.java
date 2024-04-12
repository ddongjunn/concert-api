package com.api.concert.domain.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConcertService {

    private final IConcertRepository iConcertRepository;

    public List<Concert> findAvailableConcerts() {

        return List.of();
    }
}
