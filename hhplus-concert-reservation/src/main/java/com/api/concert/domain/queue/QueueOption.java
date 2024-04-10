package com.api.concert.domain.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@Getter
@RequiredArgsConstructor
public class QueueOption {
    public static final long QUEUE_EXPIRED_TIME = 1L;
    public static final int QUEUE_LIMIT = 5;

    private final AtomicLong ongoingStatusCount = new AtomicLong();

    public void initializeQueueCount(Long count){
        this.ongoingStatusCount.set(count);
    }

    public boolean hasAvailableSpaceInOngoingQueue(){
        if(ongoingStatusCount.get() < QUEUE_LIMIT){
            this.ongoingStatusCount.incrementAndGet();
            return true;
        }
        return false;
    }

    public int calculateAvailableQueueSpace() {
        long currentOngoingCount = this.ongoingStatusCount.get();
        return (int) (QUEUE_LIMIT - currentOngoingCount);
    }

    public void incrementOngoingCount(){
        this.ongoingStatusCount.incrementAndGet();
    }

    public void decrementOngoingCount(){
        this.ongoingStatusCount.decrementAndGet();
    }
}
