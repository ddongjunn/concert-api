package com.api.concert.controller.queue.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class QueueRegisterResponse {

    private Long waitNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @Builder
    private QueueRegisterResponse(Long waitNumber, LocalDateTime expiredAt, String message){
        this.waitNumber = waitNumber;
        this.expiredAt = expiredAt;
        this.message = message;
    }
}
