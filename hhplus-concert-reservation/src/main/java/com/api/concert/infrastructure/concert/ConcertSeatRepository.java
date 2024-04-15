package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.IConcertSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ConcertSeatRepository implements IConcertSeatRepository {
    private final ConcertSeatJpaRepository concertSeatJpaRepository;
}
