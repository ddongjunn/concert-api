package com.api.concert.infrastructure.queue;

import com.api.concert.domain.queue.constant.WaitingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QueueJpaRepository extends JpaRepository<QueueEntity, Long> {
    long countByStatus(WaitingStatus status);

    boolean existsByUserIdAndStatusIn(Long userId, List<WaitingStatus> statuses);

    List<QueueEntity> findByStatusAndExpiredAtIsBefore(WaitingStatus status, LocalDateTime queueExpiredTime);

    @Query("SELECT q FROM QueueEntity q WHERE q.status = :status")
    List<QueueEntity> findWaitStatusOrderByCreatedAt(@Param("status") WaitingStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE QueueEntity q SET q.status = :status WHERE q.concertWaitingId IN :ids")
    void updateStatusByConcertWaitingIds(@Param("status") WaitingStatus status, @Param("ids") List<Long> ids);


    @Modifying
    @Query("UPDATE QueueEntity q SET q.status = :status, q.expiredAt = :expiredAt WHERE q.concertWaitingId = :id")
    void updateStatusAndExpiredAtByConcertWaitingId(@Param("status") WaitingStatus status, @Param("expiredAt") LocalDateTime expiredAt, @Param("id") Long id);
}
