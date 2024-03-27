package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.domain.Lecture;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LecturePersistenceAdapter implements LoadLecturePort {

    private final LectureRepository lectureRepository;
    private final LectureMapHelper lectureMapHelper;

    @Override
    public Lecture loadByIdAndIncrementApplicantCount(Long lectureId) {
        LectureEntity lectureEntity = lectureRepository.findByIdWithPessimisticLock(lectureId)
                .orElseThrow(EntityNotFoundException::new);

        lectureEntity.incrementApplicantCount();

        return lectureMapHelper.entityToDomain(
                lectureEntity
        );
    }
}
