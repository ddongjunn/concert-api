package com.api.concert.common.exception;

import com.api.concert.common.model.ResponseCode;
import lombok.Getter;

public class CommonException extends RuntimeException {

    @Getter
    private final ResponseCode responseCode;

    public CommonException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public CommonException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }
}