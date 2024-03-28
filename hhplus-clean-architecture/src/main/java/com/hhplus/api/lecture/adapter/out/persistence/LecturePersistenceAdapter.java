package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.LoadLecturesPort;
import com.hhplus.api.lecture.domain.Lecture;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LecturePersistenceAdapter implements LoadLecturePort, LoadLecturesPort {

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

    @Override
    public List<Lecture> load() {
        return lectureRepository.findByStartDateLessThanEqual(LocalDateTime.now())
                .stream().map(entity -> Lecture.of(entity.getId(),
                        entity.getName(),
                        entity.getApplicantCount(),
                        entity.getCapacityLimit(),
                        entity.getStartDate()))
                .collect(Collectors.toList());
    }
}
