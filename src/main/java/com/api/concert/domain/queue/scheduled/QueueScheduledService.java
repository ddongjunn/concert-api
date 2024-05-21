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

    public final static int QUEUE_LIMIT = 1000;
    public final static int QUEUE_EXPIRED_TIME = 300;

    @Scheduled(cron = "0/30 * * * * ?")
    public void activateQueues() {
        int availableQueueCount = availableQueueSpace();
        if(availableQueueCount <= 0){
            return;
        }

        List<Long> userIds = iQueueRedisRepository.pollUsersFromWaitQueue(availableQueueCount);
        iQueueRedisRepository.activate(userIds, QUEUE_EXPIRED_TIME);
    }

    public int availableQueueSpace(){
        return QUEUE_LIMIT - iQueueRedisRepository.getOngoingCount();
    }
}
