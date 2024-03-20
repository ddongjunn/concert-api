package io.hhplus.tdd.point.error;

public enum ErrorCode {
    INCORRECT_AMOUNT("포인트 충전 금액을 확인해주세요."),
    NOT_ENOUGH_POINTS("사용할 수 있는 포인트가 부족합니다. 현재 포인트 : %d");

    private final String message;

    ErrorCode(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public String getMessage(Long currentPoints) {
        return String.format(message, currentPoints);
    }

}
