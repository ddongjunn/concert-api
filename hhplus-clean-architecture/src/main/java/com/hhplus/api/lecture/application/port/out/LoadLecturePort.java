package com.hhplus.api.lecture.application.port.out;

import com.hhplus.api.lecture.domain.Lecture;

public interface LoadLecturePort {
    Lecture loadById(Long lectureId);
}
