package com.api.concert.infrastructure.point;

import jakarta.persistence.LockModeType;
import org.hibernate.LockMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<PointEntity, Long> {


    Optional<PointEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
