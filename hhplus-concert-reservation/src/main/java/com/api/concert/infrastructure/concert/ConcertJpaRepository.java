package com.api.concert.infrastructure.concert;

import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConcertJpaRepository extends JpaRepository<ConcertEntity, Long> {

}
