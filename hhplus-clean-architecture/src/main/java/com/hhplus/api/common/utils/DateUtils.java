package com.hhplus.api.common.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {
    public String FORMAT_DEFAULT = "yyyy.MM.dd HH:mm";

    public String toString(LocalDateTime value) {
        return toString(value, FORMAT_DEFAULT);
    }

    public String toString(LocalDateTime value, String format) {
        if (value == null) {
            return StringUtils.EMPTY;
        }

        try {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return value.format(formatter);
        } catch (IllegalArgumentException e) {
            return StringUtils.EMPTY;
        }
    }
}
