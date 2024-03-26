package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface LectureHistoryRepository extends JpaRepository<LectureHistoryEntity, Long> {

    Optional<LectureHistoryEntity> findByLectureIdAndUserId(Long id, Long userId);
}
