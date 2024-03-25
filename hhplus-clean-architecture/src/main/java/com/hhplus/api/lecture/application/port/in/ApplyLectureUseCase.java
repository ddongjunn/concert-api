package com.hhplus.api.lecture.application.port.in;

import com.hhplus.api.common.ResponseMessage;

public interface ApplyLectureUseCase {
    ResponseMessage apply(ApplyLectureCommand command);
}
