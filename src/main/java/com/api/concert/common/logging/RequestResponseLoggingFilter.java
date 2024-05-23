package com.api.concert.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private final String REQUEST_ID = "request_id";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(REQUEST_ID, requestId);

        long start = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        long end = System.currentTimeMillis();

        try {
            toPrettierLog(requestWrapper, responseWrapper, (end - start));
            responseWrapper.copyBodyToResponse();
        } catch (Exception e){
            log.error("logging fail : {}",e.getMessage());
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }

    private void toPrettierLog(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long elapsedTime) throws IOException {
        String requestBody = new String(request.getContentAsByteArray(), request.getCharacterEncoding());
        String responseBody = new String(response.getContentAsByteArray(), response.getCharacterEncoding());
        log.info("[REQUEST] {} {} {} ({})s",request.getMethod(), request.getRequestURI(), response.getStatus(), elapsedTime / 1000.0);
        log.info(">> REQUEST_BODY: {}", requestBody);
        log.info(">> RESPONSE_BODY: {}", responseBody);
    }

}
