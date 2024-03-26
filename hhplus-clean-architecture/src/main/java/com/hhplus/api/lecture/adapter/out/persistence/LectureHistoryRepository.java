package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LectureHistoryRepository extends JpaRepository<LectureHistoryEntity, Long> {

/*    @Query("select a from ActivityJpaEntity a " +
            "where a.ownerAccountId = :ownerAccountId " +
            "and a.timestamp >= :since")
    LectureHistoryEntity findByOwnerSince(
            @Param("ownerAccountId") Long ownerAccountId,
            @Param("since") LocalDateTime since);*/
}
