package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.IReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReservationRepository implements IReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public void save(ReservationEntity reservationEntity) {
        reservationJpaRepository.save(reservationEntity);
    }
}
