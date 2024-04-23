package com.api.concert.infrastructure.point;

import com.api.concert.domain.point.IPointHistoryRepository;
import com.api.concert.domain.point.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PointHistoryRepository implements IPointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public void save(PointHistoryEntity pointHistoryEntity) {
        pointHistoryJpaRepository.save(pointHistoryEntity);
    }
}
