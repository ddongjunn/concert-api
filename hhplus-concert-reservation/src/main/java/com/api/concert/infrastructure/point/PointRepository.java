package com.api.concert.infrastructure.point;

import com.api.concert.domain.point.IPointRepository;
import com.api.concert.domain.point.Point;
import com.api.concert.domain.point.PointConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PointRepository implements IPointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Point findPointByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId)
                .map(PointConverter::toDomain)
                .orElseGet(() -> Point.builder()
                        .userId(userId)
                        .point(0L)
                        .build());
    }

    @Override
    public Point updatePoint(PointEntity pointEntity) {
        boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("repository tx active={}", txActive);

        return PointConverter.toDomain(
                pointJpaRepository.save(pointEntity)
        );
    }
}
