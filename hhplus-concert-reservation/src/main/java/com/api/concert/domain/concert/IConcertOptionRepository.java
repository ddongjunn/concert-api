package com.api.concert.domain.concert;

import com.api.concert.infrastructure.concert.projection.ConcertInfoProjection;
import com.api.concert.infrastructure.concert.projection.ReservationInfoProjection;

import java.util.List;

public interface IConcertOptionRepository {
    List<ConcertInfoProjection> availableConcerts();

    boolean existFindById(Long id);

    ReservationInfoProjection findConcertInformation(Long concertOptionId);
}
