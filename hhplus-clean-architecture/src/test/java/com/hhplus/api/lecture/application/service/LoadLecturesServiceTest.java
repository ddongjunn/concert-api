package com.hhplus.api.lecture.application.service;

import com.hhplus.api.lecture.application.port.in.LoadLecturesUseCase;
import com.hhplus.api.lecture.application.port.out.LoadLecturesPort;
import com.hhplus.api.lecture.domain.Lecture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadLecturesServiceTest {

    @Mock
    private LoadLecturesPort loadLecturesPort;

    @InjectMocks
    private LoadLecturesService loadLecturesService;

    @DisplayName("신청 가능한 강의 목록 조회")
    @Test
    void givenLectures_whenLoadAvailableLectures_thenReturnLectures(){
        // Given
        List<Lecture> lectures = new ArrayList<>();
        lectures.add(Lecture.of(1L, "tdd",15,30, LocalDateTime.now()));
        lectures.add(Lecture.of(1L, "spring",2,30, LocalDateTime.now()));
        lectures.add(Lecture.of(1L, "kotlin",6,30, LocalDateTime.now()));

        // When
        when(loadLecturesPort.load()).thenReturn(lectures);
        List<Lecture> resultLectures = loadLecturesService.load();

        assertThat(resultLectures.size()).isEqualTo(3);
        assertThat(lectures).isEqualTo(resultLectures);
    }
}