package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.lecture.application.port.in.ApplyLectureStatusCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureStatusUseCase;
import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplyLectureStatusService implements ApplyLectureStatusUseCase {

    private final LoadLectureHistoryPort loadLectureHistoryPort;

    @Override
    public ResponseMessage applyStatus(ApplyLectureStatusCommand command) {
        boolean applicationSuccessful = loadLectureHistoryPort.loadById(command.getLectureId(), command.getUserId());
        String statusCode = applicationSuccessful ? "200" : "500";
        String message = applicationSuccessful ? "신청 성공" : "신청 실패";

        return new ResponseMessage(statusCode, message);
    }
}
