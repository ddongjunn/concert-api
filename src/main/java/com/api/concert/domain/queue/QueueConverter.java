package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.infrastructure.queue.QueueEntity;

public class QueueConverter {

    public static QueueEntity toEntity(Queue queue){
        return QueueEntity.builder()
                .queueId(queue.getQueueId())
                .userId(queue.getUserId())
                .status(queue.getStatus())
                .expiredAt(queue.getExpiredAt())
                .isExpired(queue.isExpired())
                .build();
    }

    public static Queue toDomain(QueueEntity queueEntity){
        return Queue.builder()
                .queueId(queueEntity.getQueueId())
                .userId(queueEntity.getUserId())
                .status(queueEntity.getStatus())
                .expiredAt(queueEntity.getExpiredAt())
                .isExpired(queueEntity.isExpired())
                .build();
    }
}
