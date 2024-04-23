package com.api.concert.domain.queue.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AlreadyWaitingUserException extends RuntimeException {
    public AlreadyWaitingUserException(String message) {
        super(message);
    }
    public AlreadyWaitingUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
