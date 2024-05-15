package com.api.concert.infrastructure.point;

import com.api.concert.domain.point.IPointHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PointHistoryRepository implements IPointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public void save(PointHistoryEntity pointHistoryEntity) {
        pointHistoryJpaRepository.save(pointHistoryEntity);
    }
}
