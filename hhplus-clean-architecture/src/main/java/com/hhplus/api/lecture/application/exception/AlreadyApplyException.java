package com.hhplus.api.lecture.application.exception;

public class AlreadyApplyException extends RuntimeException{
    public AlreadyApplyException(String message){
        super(message);
    }
    public AlreadyApplyException(String message, Throwable cause){
        super(message, cause);
    }
}
