package com.api.concert.infrastructure.producer;

import com.api.concert.domain.concert.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExternalPaymentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPayment(List<Reservation> message) {
        kafkaTemplate.send("payment_topic", message);
    }
}
