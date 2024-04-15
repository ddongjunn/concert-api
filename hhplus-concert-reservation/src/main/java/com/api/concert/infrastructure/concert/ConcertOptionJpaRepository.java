package com.api.concert.infrastructure.concert;

import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConcertOptionJpaRepository extends JpaRepository<ConcertOptionEntity, Long> {

    @Query(value =
            "SELECT co.concertOptionId AS concertOptionId, c.name AS name, c.singer AS singer, co.venue AS venue, co.startDate AS startDate " +
                    "FROM ConcertEntity c " +
                    "JOIN ConcertOptionEntity co ON c.concertId = co.concertId " +
                    "WHERE co.reservationStartDate < now() AND co.startDate > now()")
        //"WHERE :limit > (SELECT COUNT(*) FROM ConcertSeatEntity cs WHERE cs.concert_option_id = co.concertOptionId)")
    List<ConcertInfo> findAvailableConcerts();
}
