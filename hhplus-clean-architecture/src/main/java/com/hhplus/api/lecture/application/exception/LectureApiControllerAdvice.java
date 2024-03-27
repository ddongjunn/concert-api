package com.hhplus.api.lecture.application.exception;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.common.enums.Return;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class LectureApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AlreadyApplyException.class)
    public ResponseEntity<ResponseMessage> AlreadyApplyExceptionHandle(Exception e) {
        ResponseMessage responseMessage = new ResponseMessage(Return.FAIL.toString(), e.getMessage());
        return ResponseEntity.internalServerError().body(responseMessage);
    }

    @ExceptionHandler(LectureApplicationFullException.class)
    public ResponseEntity<ResponseMessage> LectureApplicationFullExceptionHandle(Exception e) {
        ResponseMessage responseMessage = new ResponseMessage(Return.FAIL.toString(), e.getMessage());
        return ResponseEntity.internalServerError().body(responseMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage> handleException(Exception e) {
        log.error("error: ", e);
        ResponseMessage responseMessage = new ResponseMessage("500", "에러가 발생했습니다.");
        return ResponseEntity.internalServerError().body(responseMessage);
    }
}
