package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.ConcertSeat;
import com.api.concert.domain.concert.IConcertSeatRepository;
import com.api.concert.domain.concert.constant.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ConcertSeatRepository implements IConcertSeatRepository {

    private final ConcertSeatJpaRepository concertSeatJpaRepository;

    @Override
    public List<ConcertSeat> findReservedSeats(Long concertOptionId) {
        return concertSeatJpaRepository.findByConcertOptionIdAndStatusNot(concertOptionId, SeatStatus.AVAILABLE)
                .stream()
                .map(ConcertSeatEntity::toDomain)
                .toList();
    }

    @Override
    public ConcertSeat findByConcertOptionIdAndSeatNo(Long concertOptionId, int seatNo) {
        return concertSeatJpaRepository.findByConcertOptionIdAndSeatNo(concertOptionId, seatNo)
                .map(ConcertSeatEntity::toDomain)
                .orElse(null);
    }

    @Override
    public ConcertSeat save(ConcertSeatEntity concertSeatEntity) {
        return ConcertSeatEntity.toDomain(
                concertSeatJpaRepository.save(concertSeatEntity)
        );
    }
}
