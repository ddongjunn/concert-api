package com.hhplus.api.lecture.application.port.out;

import com.hhplus.api.lecture.domain.LectureHistory;

import java.util.List;

public interface LoadLectureHistoryPort {
    boolean exitsByLectureIdAndUserId(Long lectureId, Long userId);

    List<LectureHistory> loadByLectureId(Long lectureId);
}
