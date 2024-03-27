package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.common.enums.Return;
import com.hhplus.api.common.enums.ReturnMessage;
import com.hhplus.api.lecture.application.exception.AlreadyApplyException;
import com.hhplus.api.lecture.application.exception.LectureQuotaExceededException;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureUseCase;
import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.ModifyLecturePort;
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
    private final ModifyLecturePort modifyLecturePort;
    private final LoadLectureHistoryPort loadLectureHistoryPort;
    private final WriteLectureHistoryPort writeLectureHistoryPort;

    @Transactional
    @Override
    public ResponseMessage apply(ApplyLectureCommand command) {
        checkIfAlreadyApplied(command.getLectureId(), command.getUserId());

        Lecture lecture = findLectureAndIncrementApplicantCount(command.getLectureId());
        try {
            applyApplicationForLecture(lecture);
            writeLectureHistory(command.getLectureId(), command.getUserId());
        } catch (LectureQuotaExceededException e) {
            decrementApplicantCount(lecture.getId());
            return new ResponseMessage(Return.FAIL.toString(), Return.FAIL.getDescription());
        }
        return new ResponseMessage(Return.SUCCESS.toString(), Return.SUCCESS.getDescription());
    }

    public void checkIfAlreadyApplied(Long lectureId, Long userId) {
        if(loadLectureHistoryPort.exitsByLectureIdAndUserId(lectureId, userId)){
            throw new AlreadyApplyException(ReturnMessage.ALREADY_SIGNED_UP_FOR_LECTURE.getMessage());
        }
    }

    @Transactional//t1
    public Lecture findLectureAndIncrementApplicantCount(Long lectureId){
        return loadLecturePort.loadById(lectureId);
    }

    public void writeLectureHistory(Long lectureId, Long userId) {
        writeLectureHistoryPort.save(LectureHistory.of(
                lectureId,
                userId,
                LocalDateTime.now()
        ));
    }

    @Transactional//t2
    public void applyApplicationForLecture(Lecture lecture){
        if(!lecture.isApplicationPossible()){
            throw new LectureQuotaExceededException();
        }
    }

    @Transactional()//t3
    public void decrementApplicantCount(Long lectureId){
        modifyLecturePort.decrementApplicantCountById(lectureId);
    }
}
