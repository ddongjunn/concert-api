package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureHistoryEntity;
import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureEntity;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LectureMapHelper {
    public Lecture entityToDomain(LectureEntity lectureEntity) {
        return Lecture.of(lectureEntity.getId(),
                lectureEntity.getName(),
                lectureEntity.getApplicantCount(),
                lectureEntity.getCapacityLimit(),
                lectureEntity.getStartDate()
        );
    }

    public LectureEntity domainToEntity(Lecture lecture) {
        return new LectureEntity(
                lecture.getId(),
                lecture.getName(),
                lecture.getApplicantCount(),
                lecture.getCapacityLimit(),
                lecture.getStartDate()
        );
    }

    public LectureHistoryEntity domainToEntity(LectureHistory lectureHistory) {
        return new LectureHistoryEntity(
                lectureHistory.getLectureId(),
                lectureHistory.getUserId(),
                lectureHistory.getRegisterDate()
        );
    }

    public LectureHistory entityToDomain(LectureHistoryEntity lectureHistoryEntity) {
        return LectureHistory.of(
                lectureHistoryEntity.getId(),
                lectureHistoryEntity.getLectureId(),
                lectureHistoryEntity.getUserId(),
                lectureHistoryEntity.getRegisterDate()
        );
    }

    public List<LectureHistory> entityToDomain(List<LectureHistoryEntity> entities) {
        return entities.stream()
                .map(entity ->
                        LectureHistory.of(entity.getId(),
                                entity.getLectureId(),
                                entity.getUserId(),
                                entity.getRegisterDate()))
                .collect(Collectors.toList());
    }
}
