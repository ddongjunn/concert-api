package com.hhplus.api.common.exception;

import com.hhplus.api.common.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class LectureApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleException(Exception e) {
        log.error("error: ", e);
        ResponseMessage responseMessage = new ResponseMessage("500", "에러가 발생했습니다.");
        return ResponseEntity.internalServerError().body(responseMessage);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseMessage> handleCustomException(Exception e) {
        log.error("error: ", e);
        ResponseMessage responseMessage = new ResponseMessage("500", e.getMessage());
        return ResponseEntity.internalServerError().body(responseMessage);
    }
}
