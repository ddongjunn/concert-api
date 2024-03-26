package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.application.port.out.ApplyLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.ModifyLecturePort;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LecturePersistenceAdapter implements ApplyLectureHistoryPort,
        LoadLecturePort,
        ModifyLecturePort {

    private final LectureRepository lectureRepository;
    private final LectureHistoryRepository lectureHistoryRepository;
    private final LectureMapHelper lectureMapHelper;

    @Override
    public Lecture loadById(Long lectureId) {
        return lectureMapHelper.entityToDomain(
                lectureRepository.findById(lectureId).orElseThrow(EntityNotFoundException::new)
        );
    }

    @Override
    public void modify(Lecture lecture) {
        lectureRepository.save(
                lectureMapHelper.domainToEntity(lecture)
        );
    }

    @Override
    public void save(LectureHistory lectureHistory) {
        lectureMapHelper.entityToDomain(
                lectureHistoryRepository.save(
                        lectureMapHelper.domainToEntity(lectureHistory)
                )
        );
    }

    @Override
    public boolean isApplicationExists(Long lectureId, Long userId) {
        return lectureHistoryRepository.findByLectureIdAndUserId(lectureId, userId).isPresent();
    }

}
