package com.api.concert.application;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QueueFacade {

    private final QueueService queueService;
    public QueueRegisterResponse register(QueueRegisterRequest queueRegisterRequestDto) {
        return queueService.register(queueRegisterRequestDto);
    }

    public QueueStatusResponse detail(Long concertWaitingId) {
        return queueService.detail(concertWaitingId);
    }

}
