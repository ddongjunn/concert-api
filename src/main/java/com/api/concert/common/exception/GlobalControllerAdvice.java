package com.api.concert.common.exception;

import com.api.concert.common.model.CommonResponse;
import com.api.concert.domain.point.exception.InsufficientPointsException;
import com.api.concert.domain.queue.exception.AlreadyWaitingUserException;
import com.api.concert.common.model.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(AlreadyWaitingUserException.class)
    public ResponseEntity<CommonResponse> AlreadyApplyExceptionHandle() {
        CommonResponse response = new CommonResponse(ResponseCode.ALREADY_WAITING_USER, ResponseCode.ALREADY_WAITING_USER.getMessage());
        return ResponseEntity.ok().body(response);
    }

    @ExceptionHandler(InsufficientPointsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientPointsException(InsufficientPointsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", ex.getCode());
        response.put("insufficient_point", ex.getInsufficientPoint());
        response.put("point", ex.getCurrentPoint());
        response.put("message", ex.getMessage());
        return ResponseEntity.ok().body(response);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<CommonResponse> CommonExceptionHandle(CommonException e) {
        CommonResponse response = new CommonResponse(e.getResponseCode(), e.getMessage());
        return ResponseEntity.ok().body(response);
    }
}
