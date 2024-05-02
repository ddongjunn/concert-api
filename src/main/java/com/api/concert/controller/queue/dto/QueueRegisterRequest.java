package com.api.concert.controller.queue.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueRegisterRequest {
    private Long userId;
    private final String key = "user:queue";
}
