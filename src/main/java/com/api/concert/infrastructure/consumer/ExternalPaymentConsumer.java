package com.api.concert.infrastructure.consumer;

import com.api.concert.domain.concert.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalPaymentConsumer {

    private final WebClient.Builder webClientBuilder;

    @KafkaListener(topics = "payment_topic", groupId = "payment_group")
    public void consume(Object obj) {
        ConsumerRecord<String, List<Reservation>> result = (ConsumerRecord<String, List<Reservation>>) obj;
        sendToExternalDataPlatform(result.value());
    }

    public void sendToExternalDataPlatform(List<Reservation> reservations) {
        WebClient webClient = webClientBuilder.baseUrl("http://localhost:8080").build();

        webClient.post()
                .uri("/api/data")
                .bodyValue(reservations)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
