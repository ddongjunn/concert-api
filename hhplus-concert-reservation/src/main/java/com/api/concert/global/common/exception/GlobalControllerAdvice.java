package com.api.concert.global.common.exception;

import com.api.concert.global.common.model.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(AlreadyWaitingUserException.class)
    public ResponseEntity<CommonResponse> AlreadyApplyExceptionHandle() {
        CommonResponse response = new CommonResponse(ReturnCode.ALREADY_WAITING_USER, ReturnCode.ALREADY_WAITING_USER.getMessage());
        return ResponseEntity.ok().body(response);
    }

}
