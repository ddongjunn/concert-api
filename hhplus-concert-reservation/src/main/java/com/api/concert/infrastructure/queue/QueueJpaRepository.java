package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public interface QueueJpaRepository extends JpaRepository<QueueEntity, Long> {
    long countByStatus(WaitingStatus status);

    boolean existsByUserIdAndStatus(Long userId, WaitingStatus status);

    List<QueueEntity> findByStatusAndExpiredAtBefore(WaitingStatus status, LocalDateTime queueExpiredTime);

    @Query("SELECT q FROM QueueEntity q WHERE q.status = 'WAIT'")
    List<QueueEntity> findWaitStatusOrderByCreatedAt(Pageable pageable);


    @Modifying
    @Query("UPDATE QueueEntity q SET q.status = :status WHERE q.concertWaitingId = :id")
    void updateStatusByConcertWaitingId(@Param("status") WaitingStatus status, @Param("id") Long id);
}
