package com.api.concert.domain.payment.event;

import com.api.concert.domain.concert.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PaymentCompletedEvent {
    private List<Reservation> reservations;
}
