package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import com.hhplus.api.lecture.application.port.out.WriteLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.ModifyLecturePort;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LecturePersistenceAdapter implements LoadLecturePort,
        ModifyLecturePort {

    private final LectureRepository lectureRepository;
    private final LectureMapHelper lectureMapHelper;

    @Override
    @Transactional
    public Lecture loadById(Long lectureId) {
        LectureEntity lectureEntity = lectureRepository.findByIdWithPessimisticLock(lectureId)
                .orElseThrow(EntityNotFoundException::new);

        lectureEntity.incrementApplicantCount();

        return lectureMapHelper.entityToDomain(
                lectureEntity
        );
    }

    @Transactional
    @Override
    public void decrementApplicantCountById(Long lectureId) {
        lectureRepository.findByIdWithPessimisticLock(lectureId)
                .orElseThrow(EntityNotFoundException::new)
                .decrementApplicantCount();
    }

}
