package com.api.concert.controller.queue;

import com.api.concert.controller.queue.dto.QueueRegisterRequest;
import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/concert/waiting/register")
    public ResponseEntity<QueueRegisterResponse> queueRegister(@RequestBody QueueRegisterRequest queueRegisterRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(queueService.register(queueRegisterRequestDto));
    }

    @GetMapping("/concert/waiting/status/{userId}")
    public ResponseEntity<QueueStatusResponse> queueDetail(@PathVariable Long userId){
        return ResponseEntity.status(HttpStatus.OK).body(queueService.detail(userId));
    }

}
