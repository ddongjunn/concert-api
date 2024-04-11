package com.api.concert.domain.queue;

import com.api.concert.controller.queue.dto.QueueRegisterResponse;
import com.api.concert.controller.queue.dto.QueueStatusResponse;
import com.api.concert.domain.queue.constant.WaitingStatus;
import com.api.concert.infrastructure.queue.QueueEntity;

public class QueueConverter {

    public static QueueEntity toEntity(Queue queue){
        return QueueEntity.builder()
                .concertWaitingId(queue.getConcertWaitingId())
                .userId(queue.getUserId())
                .status(queue.getStatus())
                .expiredAt(queue.getExpiredAt())
                .build();
    }

    public static Queue toDomain(QueueEntity queueEntity){
        return Queue.builder()
                .concertWaitingId(queueEntity.getConcertWaitingId())
                .userId(queueEntity.getUserId())
                .status(queueEntity.getStatus())
                .expiredAt(queueEntity.getExpiredAt())
                .createdAt(queueEntity.getCreatedAt())
                .updatedAt(queueEntity.getUpdatedAt())
                .build();
    }

    public static QueueRegisterResponse toRegisterResponse(Queue queue){
        String ONGOING_MESSAGE = "현재 고객님 순번으로 예약 만료시간까지 예약이 가능합니다.";

        return QueueRegisterResponse.builder()
                .waitNumber(queue.getConcertWaitingId())
                .expiredAt(queue.getExpiredAt())
                .message(queue.getStatus() == WaitingStatus.ONGOING ? ONGOING_MESSAGE : null)
                .build();
    }

    public static QueueStatusResponse toStatusResponse(Queue queue) {
        String message = String.format("현재 고객님 순번 : %s", queue.getWaitingNumber());

        return QueueStatusResponse.builder()
                .waitNumber(queue.getWaitingNumber())
                .status(queue.getStatus())
                .message(message)
                .build();
    }
}
