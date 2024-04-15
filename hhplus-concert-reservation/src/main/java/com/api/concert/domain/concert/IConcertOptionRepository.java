package com.api.concert.domain.concert;

import com.api.concert.infrastructure.concert.projection.ConcertInfo;

import java.util.List;

public interface IConcertOptionRepository {
    List<ConcertInfo> availableConcerts();
}
