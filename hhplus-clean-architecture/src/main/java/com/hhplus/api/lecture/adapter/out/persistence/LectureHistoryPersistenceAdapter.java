package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.common.exception.CustomException;
import com.hhplus.api.lecture.adapter.out.persistence.entity.LectureHistoryEntity;
import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import com.hhplus.api.lecture.domain.LectureHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LectureHistoryPersistenceAdapter implements LoadLectureHistoryPort {

    private final LectureMapHelper lectureMapHelper;
    private final LectureHistoryRepository lectureHistoryRepository;

    @Override
    public boolean loadById(Long lectureId, Long userId) {
        return lectureHistoryRepository.findByLectureIdAndUserId(lectureId, userId).isPresent();
    }
}
