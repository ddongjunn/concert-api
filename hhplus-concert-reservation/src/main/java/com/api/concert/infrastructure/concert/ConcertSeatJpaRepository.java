package com.api.concert.infrastructure.concert;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertSeatJpaRepository extends JpaRepository <ConcertSeatEntity, Long> {
}
