package com.api.concert.domain.point.event;

import com.api.concert.domain.point.constant.TransactionType;
import com.api.concert.infrastructure.point.PointHistoryEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PointHistoryLoggedEvent {
    private Long userId;
    private Long point;
    private TransactionType type;

    public static PointHistoryEntity toEntity(PointHistoryLoggedEvent pointHistory){
        return PointHistoryEntity.builder()
                .userId(pointHistory.getUserId())
                .point(pointHistory.getPoint())
                .type(pointHistory.getType())
                .build();
    }
}
