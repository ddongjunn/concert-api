package com.hhplus.api.lecture.application.exception;

public class EarlyApplicationLectureException extends RuntimeException {

    public EarlyApplicationLectureException(String message){
        super(message);
    }
    public EarlyApplicationLectureException(String message, Throwable cause){
        super(message, cause);
    }
}
