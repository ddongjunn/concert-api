package com.hhplus.api.lecture.application.port.out;

import com.hhplus.api.lecture.domain.LectureHistory;

public interface ApplyLectureHistoryPort {
    void save(LectureHistory lectureHistory);
    boolean isApplicationExists(Long LectureId, Long userId);
}
