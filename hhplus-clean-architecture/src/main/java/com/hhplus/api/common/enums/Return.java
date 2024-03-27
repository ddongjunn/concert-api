package com.hhplus.api.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Return {
    SUCCESS("성공"),
    FAIL("실패");

    private final String description;

}
