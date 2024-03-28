package com.hhplus.api.common.enums;

import com.hhplus.api.lecture.application.exception.LectureApplicationFullException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReturnMessage {
    ALREADY_SIGNED_UP_FOR_LECTURE("이미 신청한 강의 입니다."),
    LECTURE_FULL("정원이 가득 찼습니다."),
    LECTURE_BEFORE_DATE("강의 시작일 이전에 신청 할 수 없습니다");

    private final String message;
}
