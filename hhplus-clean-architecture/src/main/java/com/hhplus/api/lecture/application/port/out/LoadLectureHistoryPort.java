package com.hhplus.api.lecture.application.port.out;

import com.hhplus.api.lecture.domain.LectureHistory;

public interface LoadLectureHistoryPort {
    boolean loadById(Long lectureId, Long userId);
}
