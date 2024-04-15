package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.ConcertSeat;
import com.api.concert.domain.concert.IConcertSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ConcertSeatRepository implements IConcertSeatRepository {
    private final ConcertSeatJpaRepository concertSeatJpaRepository;

    @Override
    public List<ConcertSeat> findReservedSeats(Long concertOptionId) {
        return concertSeatJpaRepository.findByConcertOptionId(concertOptionId)
                .stream()
                .map(ConcertSeatEntity::toDomain)
                .toList();
    }
}
