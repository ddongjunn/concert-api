package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.WriteLectureHistoryPort;
import com.hhplus.api.lecture.domain.LectureHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LectureHistoryPersistenceAdapter implements LoadLectureHistoryPort,
        WriteLectureHistoryPort{

    private final LectureMapHelper lectureMapHelper;
    private final LectureHistoryRepository lectureHistoryRepository;

    @Override
    @Transactional
    public boolean exitsByLectureIdAndUserId(Long lectureId, Long userId) {
        return lectureHistoryRepository.findByLectureIdAndUserId(lectureId, userId)
                .isPresent();
    }

    @Override
    public List<LectureHistory> loadByLectureId(Long lectureId) {
        return lectureMapHelper.entityToDomain(
                lectureHistoryRepository.findByLectureId(lectureId));
    }

    @Override
    public void save(LectureHistory lectureHistory) {
        lectureHistoryRepository.save(
                lectureMapHelper.domainToEntity(lectureHistory));
    }

}
