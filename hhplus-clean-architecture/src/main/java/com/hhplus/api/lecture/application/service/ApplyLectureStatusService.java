package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.common.enums.Return;
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
        boolean exits = loadLectureHistoryPort.exitsByLectureIdAndUserId(command.getLectureId(), command.getUserId());
        Return responseCode = exits ? Return.SUCCESS : Return.FAIL;
        return new ResponseMessage(responseCode.toString(), responseCode.getDescription());
    }
}
