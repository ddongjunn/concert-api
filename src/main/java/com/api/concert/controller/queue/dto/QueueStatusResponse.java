package com.api.concert.controller.queue.dto;

import com.api.concert.domain.queue.constant.WaitingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class QueueStatusResponse {
    private int waitNumber;
    private WaitingStatus status;
    private String message;

    @Builder
    private QueueStatusResponse(int waitNumber, WaitingStatus status, String message){
        this.waitNumber = waitNumber;
        this.status = status;
        this.message = message;
    }
}
