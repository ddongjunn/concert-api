package com.hhplus.api.lecture.application.port.out;

import com.hhplus.api.lecture.domain.LectureHistory;

public interface ApplyLectureHistoryPort {
    LectureHistory save(LectureHistory lectureHistory);
}
