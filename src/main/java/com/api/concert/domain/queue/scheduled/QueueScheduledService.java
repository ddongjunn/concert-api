package com.api.concert.domain.queue.scheduled;

import com.api.concert.domain.queue.IQueueRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueScheduledService {

    private final IQueueRedisRepository iQueueRedisRepository;

    public final static int QUEUE_LIMIT = 5;
    public final static int QUEUE_EXPIRED_TIME = 60;

    @Scheduled(cron = "0/30 * * * * ?")
    public void activateQueues() {
        int ongoingCount = availableQueueSpace();
        if(ongoingCount < 0){
            return;
        }

        List<Long> userIds = iQueueRedisRepository.getUserToActivate(ongoingCount);
        iQueueRedisRepository.activate(userIds, QUEUE_EXPIRED_TIME);
    }

    public int availableQueueSpace(){
        return QUEUE_LIMIT - iQueueRedisRepository.getOngoingCount();
    }
}
