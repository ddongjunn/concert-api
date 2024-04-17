package com.api.concert.domain.concert;

import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import com.api.concert.infrastructure.concert.projection.ReservationInfo;

import java.util.List;

public interface IConcertOptionRepository {
    List<ConcertInfo> availableConcerts();

    boolean existFindById(Long id);

    ReservationInfo findConcertInformation(Long concertOptionId);
}
