package com.api.concert.global.common.model;

import com.api.concert.global.common.exception.ReturnCode;

public record CommonResponse(ReturnCode code, String message) {
}
