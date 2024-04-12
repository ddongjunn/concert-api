package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.Concert;
import com.api.concert.domain.concert.IConcertRepository;
import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ConcertRepository implements IConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public List<ConcertInfo> availableConcerts() {
        return concertJpaRepository.findAvailableConcerts();
    }
}
