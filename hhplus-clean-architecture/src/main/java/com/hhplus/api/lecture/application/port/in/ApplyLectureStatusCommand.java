package com.hhplus.api.lecture.application.port.in;

import com.hhplus.api.common.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class ApplyLectureStatusCommand extends SelfValidating<ApplyLectureCommand> {

    @NotNull Long lectureId;

    @NotNull Long userId;

    public ApplyLectureStatusCommand(
            Long lectureId,
            Long userId){
        this.lectureId = lectureId;
        this.userId = userId;
        this.validateSelf();
    }
}
