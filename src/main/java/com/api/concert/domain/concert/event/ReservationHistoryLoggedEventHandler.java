package com.api.concert.domain.concert.event;

import com.api.concert.domain.concert.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ReservationHistoryLoggedEventHandler {

    private final ReservationService reservationService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(
            classes = ReservationHistoryLoggedEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(ReservationHistoryLoggedEvent event) {
        event.getReservations().forEach(reservationService::save);
    }
}
