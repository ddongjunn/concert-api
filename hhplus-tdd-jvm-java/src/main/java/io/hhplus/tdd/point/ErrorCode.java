package io.hhplus.tdd.point;

public enum ErrorCode {
    INCORRECT_AMOUNT("포인트 충전 금액을 확인해주세요.");

    private final String code;

    ErrorCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
