package com.api.concert.global.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReturnCode {
    ALREADY_WAITING_USER("이미 대기중인 사용자");

    private final String message;
}
