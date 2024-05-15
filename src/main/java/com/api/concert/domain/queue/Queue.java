package com.api.concert.domain.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.common.exception.CommonException;
import com.api.concert.common.model.ResponseCode;
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

    private Long queueId;
    private Long userId;
    private int waitingNumber;
    private WaitingStatus status;
    private LocalDateTime expiredAt;
    private boolean isExpired;

    @Builder
    public Queue(Long queueId, Long userId, WaitingStatus status, boolean isExpired) {
        this.queueId = queueId;
        this.userId = userId;
        this.status = status;
        this.isExpired = isExpired;
    }

    public void updateWaitingNumber(int ranking) {
        this.waitingNumber = ranking;
    }

    public void expiry() {
        this.status = null;
        this.isExpired = true;
    }

    public void toWait(){
        this.status = WaitingStatus.WAIT;
    }

    public void toOngoing(final long QUEUE_EXPIRED_TIME){
        this.status = WaitingStatus.ONGOING;
        this.expiredAt = LocalDateTime.now().plusMinutes(QUEUE_EXPIRED_TIME);
    }

    public void assertNotWait() {
        if(this.status == WaitingStatus.WAIT){
            throw new CommonException(ResponseCode.ALREADY_WAITING_USER, ResponseCode.ALREADY_WAITING_USER.getMessage());
        }
    }

    public void assertNotOngoing() {
        if(this.status == WaitingStatus.ONGOING){
            String message = String.format("대기열 만료 시간 [%s]", this.getExpiredAt());
            throw new CommonException(ResponseCode.ALREADY_ONGOING_USER, message);
        }
    }

}
