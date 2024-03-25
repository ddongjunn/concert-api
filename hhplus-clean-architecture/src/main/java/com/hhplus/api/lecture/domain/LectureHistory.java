package com.hhplus.api.lecture.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LectureHistory {
    Long id;
    Long lectureId;
    Long userId;
    LocalDateTime registerDate;
}