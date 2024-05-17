package com.api.concert.infrastructure.concert;

import com.api.concert.infrastructure.concert.projection.ConcertInfoProjection;
import com.api.concert.infrastructure.concert.projection.ReservationInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConcertOptionJpaRepository extends JpaRepository<ConcertOptionEntity, Long> {

    @Query(value =
            "SELECT co.concertOptionId AS concertOptionId, c.name AS name, c.singer AS singer, co.venue AS venue, co.startDate AS startDate " +
            "FROM ConcertEntity c JOIN ConcertOptionEntity co ON c.concertId = co.concertId " +
            "WHERE co.reservationStartDate < now() AND co.startDate > now()")
    List<ConcertInfoProjection> findAvailableConcerts();

}
