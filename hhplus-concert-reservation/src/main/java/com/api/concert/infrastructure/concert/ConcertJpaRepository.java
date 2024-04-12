package com.api.concert.infrastructure.concert;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertJpaRepository extends JpaRepository<Long, ConcertEntity> {

    List<ConcertEntity> findAvailableConcerts();
}
