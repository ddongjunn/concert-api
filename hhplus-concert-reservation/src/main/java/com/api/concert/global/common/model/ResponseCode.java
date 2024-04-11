package com.api.concert.global.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    ALREADY_WAITING_USER("이미 대기열에 등록된 사용자"),
    ALREADY_ONGOING_USER("현재 콘서트 예약 진행가능한 상태"),
    TICKET_NOT_ISSUED("번호표의 정보가 존재하지 않습니다."),
    POINT_CHARGE_NEGATIVE("포인트 충전은 양수만 가능합니다."),
    NOT_ENOUGH_POINT("사용가능한 포인트 부족");
    private final String message;
}
