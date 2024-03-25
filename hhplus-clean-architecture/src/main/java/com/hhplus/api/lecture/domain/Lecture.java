package com.hhplus.api.lecture.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Lecture {
    Long id;
    String name;
    int currentAttendees;
    LocalDateTime startDate;
}
