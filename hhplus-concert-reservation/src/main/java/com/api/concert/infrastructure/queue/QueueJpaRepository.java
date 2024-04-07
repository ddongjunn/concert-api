package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueJpaRepository extends JpaRepository<QueueEntity, Long> {
    long countByStatus(WaitingStatus status);

    boolean existsByUserIdAndStatus(Long userId, WaitingStatus status);
}
