package com.api.concert.controller.payment.dto;

import com.api.concert.global.common.model.ResponseCode;
import lombok.Builder;

@Builder
public record PaymentResponse (ResponseCode code) {
}
