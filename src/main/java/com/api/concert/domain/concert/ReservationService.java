package com.api.concert.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final IReservationRepository iReservationRepository;

    public void save(Reservation reservation) {
        iReservationRepository.save(
                Reservation.toEntity(reservation)
        );
    }
}
