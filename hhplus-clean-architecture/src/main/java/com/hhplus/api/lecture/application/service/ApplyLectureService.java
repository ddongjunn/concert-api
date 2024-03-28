package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.common.enums.Return;
import com.hhplus.api.common.enums.ReturnMessage;
import com.hhplus.api.lecture.application.exception.AlreadyApplyException;
import com.hhplus.api.lecture.application.exception.EarlyApplicationLectureException;
import com.hhplus.api.lecture.application.exception.LectureApplicationFullException;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureUseCase;
import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.WriteLectureHistoryPort;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApplyLectureService implements ApplyLectureUseCase {

    private final LoadLecturePort loadLecturePort;
    private final LoadLectureHistoryPort loadLectureHistoryPort;
    private final WriteLectureHistoryPort writeLectureHistoryPort;

    @Transactional
    @Override
    public ResponseMessage apply(ApplyLectureCommand command) {
        Lecture lecture = findLectureAndIncrementApplicantCount(command.getLectureId());
        lectureApplyValidation(command, lecture);
        writeLectureHistory(command.getLectureId(), command.getUserId());
        return new ResponseMessage(Return.SUCCESS.toString(), Return.SUCCESS.getDescription());
    }

    public Lecture findLectureAndIncrementApplicantCount(Long lectureId){
        return loadLecturePort.loadByIdAndIncrementApplicantCount(lectureId);
    }

    public void lectureApplyValidation(ApplyLectureCommand command, Lecture lecture){
        checkApplicationStartDate(lecture);
        checkAlreadyApplied(command.getLectureId(), command.getUserId()); //row가 없으면 lock안 걸림.
        applyApplicationForLecture(lecture);
    }

    public void checkApplicationStartDate(Lecture lecture){
        if(!lecture.isApplicationDateAvailable()){
            throw new EarlyApplicationLectureException(ReturnMessage.LECTURE_BEFORE_DATE.getMessage());
        }
    }

    public void checkAlreadyApplied(Long lectureId, Long userId) {
        if(loadLectureHistoryPort.exitsByLectureIdAndUserId(lectureId, userId)){
            throw new AlreadyApplyException(ReturnMessage.ALREADY_SIGNED_UP_FOR_LECTURE.getMessage());
        }
    }

    public void applyApplicationForLecture(Lecture lecture){
        if(!lecture.isApplicationPossible()){
            throw new LectureApplicationFullException(ReturnMessage.LECTURE_FULL.getMessage());
        }
    }

    public void writeLectureHistory(Long lectureId, Long userId) {
        writeLectureHistoryPort.save(LectureHistory.of(
                lectureId,
                userId,
                LocalDateTime.now()
        ));
    }
}