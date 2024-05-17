package com.api.concert.domain.payment.event;

import com.api.concert.infrastructure.producer.ExternalPaymentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentCompletedEventHandler {

    private final ExternalPaymentProducer externalPaymentProducer;

    @EventListener(PaymentCompletedEvent.class)
    public void handle(PaymentCompletedEvent event) {
        externalPaymentProducer.sendPayment(event.getReservations());
    }
}
