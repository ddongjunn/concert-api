package com.api.concert.api.queue.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueueController {

    @PostMapping("/concert/waiting/register")
    public String queueRegister(){
        return """
                {
                  "wait_number": 5
                }
                """;
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
