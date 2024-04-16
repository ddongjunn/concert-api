package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.IConcertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ConcertRepository implements IConcertRepository {
    private final ConcertJpaRepository concertJpaRepository;
}
