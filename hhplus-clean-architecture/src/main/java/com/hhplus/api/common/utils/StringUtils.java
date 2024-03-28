package com.hhplus.api.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public static final String EMPTY = "";

    public boolean isEmpty(String s) {
        return s == null || s.length() < 1;
    }

    public boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }
    public String logLineBar() {
        return "##############################";
    }
}
