package com.api.concert.global.common.exception;

import com.api.concert.global.common.model.ResponseCode;
import lombok.Getter;

@Getter
public class InsufficientPointsException extends RuntimeException{
    private final ResponseCode code;
    private final long insufficientPoint;
    private final long currentPoint;

    public InsufficientPointsException(String message, ResponseCode code, long insufficientPoint, long currentPoint) {
        super(message);
        this.code = code;
        this.insufficientPoint = insufficientPoint;
        this.currentPoint = currentPoint;
    }
}
