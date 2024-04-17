package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.ConcertSeat;
import com.api.concert.domain.concert.constant.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertSeatJpaRepository extends JpaRepository <ConcertSeatEntity, Long> {

    @Modifying
    @Query("UPDATE ConcertSeatEntity c set c.status = :status, c.userId = :userId WHERE c.seatId IN :ids")
    void updateStatusAndUserIdByStatus(@Param("status")SeatStatus status, @Param("userId")Long userId, @Param("ids")List<Long> ids);

    @Query("SELECT c FROM ConcertSeatEntity c where c.concertOptionId = :id AND c.status != :status")
    List<ConcertSeatEntity> findByConcertOptionIdAndStatusNot(@Param("id") Long concertOptionId, @Param("status") SeatStatus status);

    Optional<ConcertSeatEntity> findByConcertOptionIdAndSeatNo(Long concertOptionId, int seatNo);

    List<ConcertSeatEntity> findByStatusAndUpdatedAtLessThanEqual(SeatStatus status, LocalDateTime time);

    List<ConcertSeatEntity> findByUserIdAndStatus(Long userId, SeatStatus seatStatus);
}
