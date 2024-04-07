package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Queue {

    private final int QUEUE_LIMIT = 5;

    private Long concertWaitingId;
    private Long userId;
    private WaitingStatus status;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Queue(Long userId) {
        this.userId = userId;
    }

    public static Queue of(Long userId){
        return new Queue(userId);
    }

    public void updateStatusForOngoingCount(long ongoingCount) {
        if(ongoingCount >= QUEUE_LIMIT){
            setWaitingStatus(WaitingStatus.WAIT);
        }

        if(ongoingCount < QUEUE_LIMIT){
            setWaitingStatus(WaitingStatus.ONGOING);
            updateQueueExpiredAt();
        }
    }

    private void setWaitingStatus(WaitingStatus waitingStatus){
        this.status = waitingStatus;
    }
    private void updateQueueExpiredAt(){
        this.expiredAt = LocalDateTime.now().plusMinutes(10);
    }
}
