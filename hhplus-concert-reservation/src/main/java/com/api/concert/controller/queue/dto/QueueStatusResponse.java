package com.api.concert.controller.queue.dto;

import com.api.concert.domain.queue.constant.WaitingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

public class QueueStatusResponse {
    private Long waitNumber;

    private WaitingStatus status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;

    @Builder
    private QueueStatusResponse(Long waitNumber, WaitingStatus status, String message, LocalDateTime expiredAt){
        this.waitNumber = waitNumber;
        this.status = status;
        this.message = message;
        this.expiredAt = expiredAt;
    }
}
