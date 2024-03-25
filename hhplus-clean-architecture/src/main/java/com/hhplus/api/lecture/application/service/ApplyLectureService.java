package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureUseCase;
import com.hhplus.api.lecture.application.port.out.ApplyLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.ModifyLecturePort;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ApplyLectureService implements ApplyLectureUseCase {

    private final LoadLecturePort loadLecturePort;
    private final ModifyLecturePort modifyLecturePort;
    private final ApplyLectureHistoryPort applyLectureHistoryPort;

    @Override
    @Transactional
    public ResponseMessage apply(ApplyLectureCommand command) {
        Lecture lecture = loadLecturePort.loadById(command.getLectureId());
        if(!lecture.isApplicationPossible()){
            return new ResponseMessage("에러", "정원 초과");
        }

        lecture.incrementApplicantCount();

        modifyLecturePort.modify(lecture);

        applyLectureHistoryPort.save(
                LectureHistory.of(lecture.getId(),
                        command.getUserId(),
                        LocalDateTime.now()
                )
        );

        return new ResponseMessage("성공", "신청 완료");
    }

}
