package com.api.concert.global.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    ALREADY_WAITING_USER("이미 대기중인 사용자"),
    ALREADY_ONGOING_USER("현재 예약 진행가능한 상태"),
    TICKET_NOT_ISSUED("번호표의 정보가 존재하지 않습니다.");

    private final String message;
}
