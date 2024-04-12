package com.api.concert.domain.concert;

import java.util.List;

public interface IConcertRepository {
    List<Concert> findAvailableConcerts();
}
