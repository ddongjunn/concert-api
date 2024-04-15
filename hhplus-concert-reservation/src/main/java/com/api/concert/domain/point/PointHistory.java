package com.api.concert.domain.point;

import com.api.concert.domain.point.constant.TransactionType;
import com.api.concert.infrastructure.point.PointEntity;
import com.api.concert.infrastructure.point.PointHistoryEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointHistory {

    private Long userId;
    private Long point;
    private TransactionType type;

    @Builder
    public PointHistory(Long userId, Long point, TransactionType type) {
        this.userId = userId;
        this.point = point;
        this.type = type;
    }

    public static PointHistoryEntity toEntity(PointHistory pointHistory){
        return PointHistoryEntity.builder()
                .userId(pointHistory.userId)
                .point(pointHistory.point)
                .type(pointHistory.type)
                .build();
    }

}
