package com.api.concert.infrastructure.concert;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcertSeatJpaRepository extends JpaRepository <ConcertSeatEntity, Long> {
    List<ConcertSeatEntity> findByConcertOptionId(Long concertOptionId);

}
