package com.api.concert.domain.concert;

import com.api.concert.infrastructure.concert.ReservationEntity;

public interface IReservationRepository {
    void save(ReservationEntity entity);
}
