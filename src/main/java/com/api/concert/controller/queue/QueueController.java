package com.api.concert.controller.queue;

import com.api.concert.application.QueueFacade;
import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QueueController {

    private final QueueFacade queueFacade;

    @PostMapping("/concert/waiting/register")
    public ResponseEntity<QueueRegisterResponse> queueRegister(@RequestBody QueueRegisterRequest queueRegisterRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(queueFacade.register(queueRegisterRequestDto));
    }

    @GetMapping("/concert/waiting/status/{concertWaitingId}")
    public ResponseEntity<QueueStatusResponse> queueDetail(@PathVariable Long concertWaitingId){
        return ResponseEntity.status(HttpStatus.OK).body(queueFacade.detail(concertWaitingId));
    }


}
