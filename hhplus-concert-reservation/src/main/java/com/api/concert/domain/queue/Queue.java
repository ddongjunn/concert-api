package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Queue {

    private Long concertWaitingId;
    private Long userId;
    private WaitingStatus status;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Queue(Long userId) {
        this.userId = userId;
    }

    public void toDone() {
        this.status = WaitingStatus.DONE;
    }

    public void toWait(){
        this.status = WaitingStatus.WAIT;
    }
    public void toOngoing(final long QUEUE_EXPIRED_TIME){
        this.status = WaitingStatus.ONGOING;
        this.expiredAt = LocalDateTime.now().plusMinutes(QUEUE_EXPIRED_TIME);
    }
}
