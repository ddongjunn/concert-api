package com.hhplus.api.lecture.application.port.in;

import com.hhplus.api.common.ResponseMessage;

public interface ApplyLectureStatusUseCase {
    ResponseMessage applyStatus(ApplyLectureStatusCommand command);
}
