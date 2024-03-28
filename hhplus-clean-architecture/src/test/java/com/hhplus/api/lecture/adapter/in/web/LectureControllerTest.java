package com.hhplus.api.lecture.adapter.in.web;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.common.enums.Return;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureStatusCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureStatusUseCase;
import com.hhplus.api.lecture.application.port.in.ApplyLectureUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LectureController.class)
class LectureControllerTest {

    private final MockMvc mvc;

    @MockBean
    private ApplyLectureUseCase applyLectureUseCase;

    @MockBean
    private ApplyLectureStatusUseCase applyLectureStatusUseCase;

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
}