package com.hhplus.api.lecture.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LectureTest {

    @Test
    public void 강의_신청이_가능한_경우(){
        // Given
        Lecture lecture = Lecture.of(1L, "java", 20, 30, LocalDateTime.now());

        // Then
        assertThat(lecture.isApplicationPossible()).isEqualTo(true);
    }

    @Test
    public void 강의_신청이_불가능한_경우(){
        // Given
        Lecture lecture = Lecture.of(1L, "java", 31, 30, LocalDateTime.now());

        // Then
        assertThat(lecture.isApplicationPossible()).isEqualTo(false);
    }


}