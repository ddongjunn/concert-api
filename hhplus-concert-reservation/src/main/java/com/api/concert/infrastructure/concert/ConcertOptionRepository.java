package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.IConcertOptionRepository;
import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ConcertOptionRepository implements IConcertOptionRepository {

    private final ConcertOptionJpaRepository concertOptionJpaRepository;
    @Override
    public List<ConcertInfo> availableConcerts() {
        return concertOptionJpaRepository.findAvailableConcerts();
    }
}
