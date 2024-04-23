package com.api.concert.global.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    ALREADY_WAITING_USER("이미 대기열에 등록된 사용자"),
    ALREADY_ONGOING_USER("현재 콘서트 예약 진행가능한 상태"),
    TICKET_NOT_ISSUED("존재하지 않는 번호표"),
    POINT_CHARGE_NEGATIVE("포인트 충전은 양수만 가능합니다."),
    NOT_ENOUGH_POINT("사용가능한 포인트 부족"),
    NO_SEATS_AVAILABLE("예약 가능한 좌석이 없습니다."),
    NO_CONCERT_AVAILABLE("예약 가능한 콘서트가 없습니다."),
    NOT_EXIST_SEAT("존재 하지 않는 좌석"),
    NOT_EXIST_CONCERT("존재 하지 않는 콘서트"),
    ALREADY_RESERVED_SEAT("이미 예약된 좌석"),
    SEAT_UNAVAILABLE("예약된 좌석이 없습니다."),
    TOKEN_DOES_NOT_EXIST("번호표를 확인해주세요."),
    INVALID_WAIT_INFORMATION("만료된 번호표"),
    SUCCESS("성공"),
    FAIL("실패");

    private final String message;
}
