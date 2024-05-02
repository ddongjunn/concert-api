package com.api.concert.controller.interceptors;

import com.api.concert.domain.queue.QueueService;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class QueueStatusCheckInterceptor implements HandlerInterceptor {

    private final QueueService queueService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String concertWaitingId = request.getHeader("Authorization");
        if(concertWaitingId == null){
            throw new CommonException(ResponseCode.TOKEN_DOES_NOT_EXIST, ResponseCode.TOKEN_DOES_NOT_EXIST.getMessage());
        }

        /*boolean isOngoing = queueService.isQueueOngoing(Long.valueOf(concertWaitingId));
        if(!isOngoing){
            throw new CommonException(ResponseCode.INVALID_WAIT_INFORMATION, ResponseCode.INVALID_WAIT_INFORMATION.getMessage());
        }*/

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
