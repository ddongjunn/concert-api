package com.hhplus.api.lecture.adapter.in.web;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.common.enums.Return;
import com.hhplus.api.lecture.application.port.in.*;
import com.hhplus.api.lecture.domain.Lecture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LectureController.class)
class LectureControllerTest {

    private final MockMvc mvc;

    @MockBean
    private ApplyLectureUseCase applyLectureUseCase;

    @MockBean
    private ApplyLectureStatusUseCase applyLectureStatusUseCase;

    @MockBean
    private LoadLecturesUseCase loadLecturesUseCase;

    @Autowired
    LectureControllerTest(MockMvc mvc){
        this.mvc = mvc;
    }


    @DisplayName("[POST] 강의 신청")
    @Test
    void givenApplyLectureCommand_whenRequesting_thenApplyLecture() throws Exception {
        // Given
        ApplyLectureCommand applyLectureCommand = new ApplyLectureCommand(1L, 1L);
        given(applyLectureUseCase.apply(any())).willReturn(new ResponseMessage(Return.SUCCESS.name(), Return.SUCCESS.getDescription()));

        // When
        mvc.perform(post("/lectures/{lectureId}/apply/{userId}", "1", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Then
        then(applyLectureUseCase).should().apply(applyLectureCommand);
    }

    @DisplayName("[GET] 강의 신청 조회")
    @Test
    void givenApplyLectureCommand_whenRequestingApplyLectureStatus_thenReturnSuccess() throws Exception {
        // Given
        Long lectureId = 1L;
        Long userId = 1L;
        ApplyLectureStatusCommand applyLectureStatusCommand = new ApplyLectureStatusCommand(lectureId, userId);
        given(applyLectureStatusUseCase.applyStatus(any())).willReturn(new ResponseMessage(Return.SUCCESS.name(), Return.SUCCESS.getDescription()));

        // When
        mvc.perform(get("/lectures/{lectureId}/check/{userId}", "1", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Then
        then(applyLectureStatusUseCase).should().applyStatus(applyLectureStatusCommand);
    }

    @DisplayName("[GET] 신청 가능한 강의 목록 조회")
    @Test
    void given_whenRequestingApplyAvailableLectures_thenReturnLectures() throws Exception {
        // Given
        List<Lecture> lectures = new ArrayList<>();
        lectures.add(Lecture.of(1L, "tdd",15,30, LocalDateTime.of(2024,3,22,13,0)));
        lectures.add(Lecture.of(2L, "spring",2,30, LocalDateTime.of(2024,3,24,13,0)));
        given(loadLecturesUseCase.load()).willReturn(lectures);

        // When
        mvc.perform(get("/lectures"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.정보").exists())
                .andExpect(jsonPath("$.정보.['현재 신청 가능한 강의 수']").value(2))
                .andExpect(jsonPath("$.강의목록").exists())
                .andExpect(jsonPath("$.강의목록[0]['강의 코드']").value("1"))
                .andExpect(jsonPath("$.강의목록[0]['강의명']").value("tdd"))
                .andExpect(jsonPath("$.강의목록[0]['현재 수강 인원']").value("15"))
                .andExpect(jsonPath("$.강의목록[0]['신청 가능 인원']").value("30"))
                .andExpect(jsonPath("$.강의목록[0]['강의 시작일']").value("2024-03-22 13:00:00"))
                .andExpect(jsonPath("$.강의목록[1]['강의 코드']").value("2"))
                .andExpect(jsonPath("$.강의목록[1]['강의명']").value("spring"))
                .andExpect(jsonPath("$.강의목록[1]['현재 수강 인원']").value("2"))
                .andExpect(jsonPath("$.강의목록[1]['신청 가능 인원']").value("30"))
                .andExpect(jsonPath("$.강의목록[1]['강의 시작일']").value("2024-03-24 13:00:00"))
                .andReturn();

        // Then
        then(loadLecturesUseCase).should().load();
    }
}