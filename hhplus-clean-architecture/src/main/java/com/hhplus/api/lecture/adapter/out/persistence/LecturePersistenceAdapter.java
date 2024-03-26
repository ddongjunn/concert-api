package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import com.hhplus.api.lecture.application.port.out.ApplyLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.ModifyLecturePort;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Persistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public LectureHistory save(LectureHistory lectureHistory) {
        return lectureMapHelper.entityToDomain(
                lectureHistoryRepository.save(
                        lectureMapHelper.domainToEntity(lectureHistory)
                )
        );
    }

}
