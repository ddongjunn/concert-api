package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.IConcertOptionRepository;
import com.api.concert.infrastructure.concert.projection.ConcertInfoProjection;
import com.api.concert.infrastructure.concert.projection.ReservationInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ConcertOptionRepository implements IConcertOptionRepository {

    private final ConcertOptionJpaRepository concertOptionJpaRepository;
    @Override
    public List<ConcertInfoProjection> availableConcerts() {
        return concertOptionJpaRepository.findAvailableConcerts();
    }

    @Override
    public boolean existFindById(Long id) {
        return concertOptionJpaRepository.existsById(id);
    }

    @Override
    public ReservationInfoProjection findConcertInformation(Long concertOptionId) {
        return concertOptionJpaRepository.findConcertInformationById(concertOptionId);
    }
}
