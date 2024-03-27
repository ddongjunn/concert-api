package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureHistoryEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface LectureHistoryRepository extends JpaRepository<LectureHistoryEntity, Long> {

    @Query(value = "SELECT a FROM LectureHistoryEntity a WHERE a.lectureId = :lectureId AND a.userId = :userId")
    Optional<LectureHistoryEntity> findByLectureIdAndUserId(Long lectureId, Long userId);

    boolean existsByLectureIdAndUserId(Long lectureId, Long userId);

    List<LectureHistoryEntity> findByLectureId(Long lectureId);
}
