package com.api.concert.controller.queue;

import com.api.concert.application.QueueFacade;
import com.api.concert.controller.queue.dto.QueueRequest;
import com.api.concert.controller.queue.dto.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QueueController {

    private final QueueFacade queueFacade;

    @PostMapping("/concert/waiting/register")
    public ResponseEntity<QueueResponse> queueRegister(@RequestBody QueueRequest queueRequestDto){
        return ResponseEntity.status(HttpStatus.OK).body(queueFacade.register(queueRequestDto));
    }

    @GetMapping("/concert/waiting/status/{userId}")
    public String queueDetail(@PathVariable Long userId){
        return """
                {
                  "code": "WAIT",
                  "message": "고객님의 순번까지 N명 남았습니다."
                }
                """;
    }


}
