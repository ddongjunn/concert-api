package com.hhplus.api.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReturnMessage {
    ALREADY_SIGNED_UP_FOR_LECTURE("이미 신청한 강의 입니다.");

    private final String message;
}
