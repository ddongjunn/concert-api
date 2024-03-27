package com.hhplus.api.lecture.application.exception;

public class LectureApplicationFullException extends RuntimeException{
    public LectureApplicationFullException(String message){
        super(message);
    }
    public LectureApplicationFullException(String message, Throwable cause){
        super(message, cause);
    }
}
