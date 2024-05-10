package com.api.concert.controller.queue.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueueStatusResponse {
    private Integer rank;
    private String expiredTime;

    @Builder
    private QueueStatusResponse(Integer rank, String expiredTime){
        this.rank = rank;
        this.expiredTime = expiredTime;
    }
}
