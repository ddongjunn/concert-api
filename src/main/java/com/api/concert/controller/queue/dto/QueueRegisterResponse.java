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

    private int rank;

    @Builder
    private QueueRegisterResponse(int rank){
        this.rank = rank;
    }
}
