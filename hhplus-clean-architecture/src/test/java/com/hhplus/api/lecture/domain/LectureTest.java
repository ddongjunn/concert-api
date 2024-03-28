package com.hhplus.api.lecture.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LectureTest {

    @DisplayName("강의 정원이 남은 경우")
    @Test
    public void 강의_신청이_가능한_경우(){
        // Given
        Lecture lecture = Lecture.of(1L, "java", 20, 30, LocalDateTime.now());

        // Then
        assertThat(lecture.isApplicationPossible()).isEqualTo(true);
    }

    @DisplayName("강의 정원이 가득 찬 경우")
    @Test
    public void 강의_신청이_불가능한_경우(){
        // Given
        Lecture lecture = Lecture.of(1L, "java", 31, 30, LocalDateTime.now());

        // Then
        assertThat(lecture.isApplicationPossible()).isEqualTo(false);
    }

    @DisplayName("강의 신청 시작일 이후에 신청하는 경우")
    @Test
    public void 강의_신청_시작일_이후(){
        LocalDateTime startDate = LocalDateTime.of(2024, 3, 25, 2, 13, 1);
        Lecture lecture = Lecture.of(1L, "java", 31, 30, startDate);
        assertThat(lecture.isApplicationDateAvailable()).isEqualTo(true);
    }

    @DisplayName("강의 신청 시작일 이전에 신청하는 경우")
    @Test
    public void 강의_신청_시작일_이전(){
        LocalDateTime startDate = LocalDateTime.of(2025, 5, 31, 2, 13, 1);
        Lecture lecture = Lecture.of(1L, "java", 31, 30, startDate);
        assertThat(lecture.isApplicationDateAvailable()).isEqualTo(false);
    }


}