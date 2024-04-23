package com.api.concert.domain.point;

import com.api.concert.infrastructure.point.PointEntity;

public interface IPointRepository {
    Point findPointByUserId(Long userId);

    Point updatePoint(PointEntity pointEntity);
}
