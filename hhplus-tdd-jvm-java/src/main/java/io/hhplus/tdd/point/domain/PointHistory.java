package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.domain.constant.TransactionType;

public record PointHistory(
        Long id,
        Long userId,
        TransactionType type,
        Long amount,
        Long timeMillis
) {
}
