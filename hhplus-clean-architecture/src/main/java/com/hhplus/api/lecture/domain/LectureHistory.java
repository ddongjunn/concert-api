package com.hhplus.api.lecture.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LectureHistory {
    Long id;
    Long lectureId;
    Long userId;
    LocalDateTime registerDate;

    public LectureHistory(Long lectureId, Long userId, LocalDateTime registerDate){
        this.lectureId = lectureId;
        this.userId = userId;
        this.registerDate = registerDate;
    }

    public static LectureHistory of(Long id, Long lectureId, Long userId, LocalDateTime registerDate){
        return new LectureHistory(id, lectureId, userId, registerDate);
    }

    public static LectureHistory of(Long lectureId, Long userId, LocalDateTime registerDate){
        return new LectureHistory(0L, lectureId, userId, registerDate);
    }
}