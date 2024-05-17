package com.api.concert.domain.concert.event;

import com.api.concert.domain.concert.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReservationHistoryLoggedEvent {
    List<Reservation> reservations;
}
