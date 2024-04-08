package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

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

    private Queue(Long userId) {
        this.userId = userId;
    }

    public static Queue of(Long userId){
        return new Queue(userId);
    }

    public void updateStatusForOngoingCount(long ongoingCount, int limit) {
        log.info("QUEUE_LIMIT {}, ongoingCount {}", limit, ongoingCount);

        if(ongoingCount >= limit){
            setWaitingStatus(WaitingStatus.WAIT);
        }

        if(ongoingCount < limit){
            setWaitingStatus(WaitingStatus.ONGOING);
            updateQueueExpiredAt();
        }
    }

    public void setWaitingStatus(WaitingStatus status){
        this.status = status;
    }
    public void updateQueueExpiredAt(){
        this.expiredAt = LocalDateTime.now().plusMinutes(1);
    }

    public void updateStatusToDone() {
        this.status = WaitingStatus.DONE;
    }

    public void updateStatusToOngoing() {
        this.status = WaitingStatus.ONGOING;
    }
}
