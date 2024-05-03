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
    private String expiredTime;

    @Builder
    private QueueStatusResponse(String expiredTime){
        this.expiredTime = expiredTime;
    }
}
