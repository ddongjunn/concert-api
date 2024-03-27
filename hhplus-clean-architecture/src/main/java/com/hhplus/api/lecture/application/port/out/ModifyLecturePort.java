package com.hhplus.api.lecture.application.port.out;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import com.hhplus.api.lecture.domain.Lecture;

public interface ModifyLecturePort {
    void decrementApplicantCountById(Long lectureId);
}
