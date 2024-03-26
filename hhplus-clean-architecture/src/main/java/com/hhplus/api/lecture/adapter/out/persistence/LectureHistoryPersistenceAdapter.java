package com.hhplus.api.lecture.adapter.out.persistence;

import com.hhplus.api.common.exception.CustomException;
import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import com.hhplus.api.lecture.domain.LectureHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LectureHistoryPersistenceAdapter implements LoadLectureHistoryPort {

    private final LectureMapHelper lectureMapHelper;
    private final LectureHistoryRepository lectureHistoryRepository;

    @Override
    public LectureHistory loadById(Long lectureId, Long userId) {
        return lectureMapHelper.entityToDomain(
                lectureHistoryRepository.findByLectureIdAndUserId(lectureId, userId)
                        .orElseThrow(() -> new CustomException("신청 실패"))
        );
    }
}
