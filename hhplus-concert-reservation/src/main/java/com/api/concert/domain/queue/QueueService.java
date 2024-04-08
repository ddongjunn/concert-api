package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRequest;
import com.api.concert.controller.queue.dto.QueueResponse;
import com.api.concert.global.common.exception.AlreadyWaitingUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final IQueueRepository iQueueRepository;

    public QueueResponse register(QueueRequest queueRequestDto){
        Long userId = queueRequestDto.getUserId();
        isUserAlreadyRegistered(userId);

        Queue queue = createQueue(userId);
        Queue savedQueue = iQueueRepository.save(QueueConverter.toEntity(queue));
        return QueueConverter.toResponse(savedQueue);
    }

    public void isUserAlreadyRegistered(Long userId) {
        if(iQueueRepository.existsByUserIdAndOngoingStatus(userId)){
            throw new AlreadyWaitingUserException();
        }
    }

    public Queue createQueue(Long userId) {
        Queue queue = Queue.of(userId);
        long ongoingCount = iQueueRepository.getCountOfOngoingStatus();
        queue.updateStatusForOngoingCount(ongoingCount);
        return queue;
    }

}
