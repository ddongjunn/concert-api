package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.constant.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertSeatJpaRepository extends JpaRepository <ConcertSeatEntity, Long> {

    @Query("SELECT c FROM ConcertSeatEntity c where c.concertOptionId = :id AND c.status != :status")
    List<ConcertSeatEntity> findByConcertOptionIdAndStatusNot(@Param("id") Long concertOptionId, @Param("status") SeatStatus status);

    Optional<ConcertSeatEntity> findByConcertOptionIdAndSeatNo(Long concertOptionId, int seatNo);

    List<ConcertSeatEntity> findByStatusAndUpdatedAtAfter(SeatStatus status, LocalDateTime time);
}
