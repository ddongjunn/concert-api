package com.hhplus.api.common.enums;

import com.hhplus.api.lecture.application.exception.LectureApplicationFullException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReturnMessage {
    ALREADY_SIGNED_UP_FOR_LECTURE("이미 신청한 강의 입니다."),
    LECTURE_FULL("정원이 가득 찼습니다.");

    private final String message;
}
