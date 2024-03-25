package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.domain.LectureHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureHistoryRepository extends JpaRepository<LectureHistory, Long> {
}
