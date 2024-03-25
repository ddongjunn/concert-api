package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.application.port.out.ApplyLecturePort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LecturePersistenceAdapter implements ApplyLecturePort {

    private final LectureHistoryRepository lectureRegistrationRepository;

}
