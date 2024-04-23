package com.api.concert.domain.point;

import com.api.concert.infrastructure.point.PointHistoryEntity;

public interface IPointHistoryRepository {
    void save(PointHistoryEntity pointHistoryEntity);
}
