package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.ConcertSeat;
import com.api.concert.domain.concert.IConcertSeatRepository;
import com.api.concert.domain.concert.constant.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    public void save(ConcertSeatEntity concertSeatEntity) {
        concertSeatJpaRepository.save(concertSeatEntity);
    }

    @Override
    public List<ConcertSeat> findExpiredTemporarySeats(SeatStatus status, LocalDateTime minusMinutes) {
        return concertSeatJpaRepository.findByStatusAndUpdatedAtLessThanEqual(status, minusMinutes)
                .stream()
                .map(ConcertSeatEntity::toDomain)
                .toList();
    }

    @Override
    public void updateStatusToExpiredBySeatIds(List<Long> expiredTemporarySeatIds) {
        concertSeatJpaRepository.updateStatusAndUserIdByStatus(SeatStatus.AVAILABLE, null, expiredTemporarySeatIds);
    }

    @Override
    public List<ConcertSeat> findTemporarilyReservedSeatsByUserId(Long userId) {
        return concertSeatJpaRepository.findByUserIdAndStatus(userId, SeatStatus.TEMPORARY)
                .stream()
                .map(ConcertSeatEntity::toDomain)
                .toList();
    }

    public void updateStatusToReserved(Long userId, List<Long> updateStatusToReservedIds) {
        concertSeatJpaRepository.updateStatusAndUserIdByStatus(SeatStatus.REVERSED, userId, updateStatusToReservedIds);
    }
}
