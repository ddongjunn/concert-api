package com.hhplus.api.lecture.application.service;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.common.enums.Return;
import com.hhplus.api.common.enums.ReturnMessage;
import com.hhplus.api.lecture.application.exception.AlreadyApplyException;
import com.hhplus.api.lecture.application.exception.LectureApplicationFullException;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.out.LoadLectureHistoryPort;
import com.hhplus.api.lecture.application.port.out.LoadLecturePort;
import com.hhplus.api.lecture.application.port.out.WriteLectureHistoryPort;
import com.hhplus.api.lecture.domain.Lecture;
import com.hhplus.api.lecture.domain.LectureHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyLectureServiceTest {

    @Mock
    private LoadLecturePort loadLecturePort;

    @Mock
    private  LoadLectureHistoryPort loadLectureHistoryPort;

    @Mock
    private  WriteLectureHistoryPort writeLectureHistoryPort;

    @Mock Lecture lecture;

    @InjectMocks
    private ApplyLectureService applyLectureService;

    @DisplayName("[강의 신청] - 성공")
    @Test
    void givenApplyLectureCommand_whenApplicationLecture_thenReturnSuccess(){
        // Given
        Long lectureId = 1L;
        Long userId = 1L;
        ApplyLectureCommand command = new ApplyLectureCommand(lectureId, userId);

        Lecture lecture = Lecture.of(1L, "spring", 0, 30, LocalDateTime.now());
        when(loadLecturePort.loadByIdAndIncrementApplicantCount(any())).thenReturn(lecture);

        ResponseMessage response = applyLectureService.apply(command);

        assertThat(response.status()).isEqualTo(Return.SUCCESS.toString());
        assertThat(response.message()).isEqualTo(Return.SUCCESS.getDescription());
    }

    @DisplayName("[강의 신청] - 정원이 가득 찬 경우 예외 발생")
    @Test
    void givenApplyLectureCommand_whenLectureApplicationFull_thenException(){
        // Given
        Long lectureId = 1L;
        Long userId = 1L;
        ApplyLectureCommand command = new ApplyLectureCommand(lectureId, userId);

        //When
        Lecture lecture = Lecture.of(1L, "spring", 31, 30, LocalDateTime.now());
        when(loadLecturePort.loadByIdAndIncrementApplicantCount(any())).thenReturn(lecture);

        assertThatThrownBy(() -> applyLectureService.apply(command))
                .isInstanceOf(LectureApplicationFullException.class)
                .hasMessage(ReturnMessage.LECTURE_FULL.getMessage());
    }

    @DisplayName("[강의 신청] - 이미 신청한 경우 예외 발생")
    @Test
    void givenApplyLectureCommand_whenAlreadyLectureApplication_thenException(){
        // Given
        Long lectureId = 1L;
        Long userId = 1L;
        ApplyLectureCommand command = new ApplyLectureCommand(lectureId, userId);

        LectureHistory lectureHistory = LectureHistory.of(lectureId, userId, LocalDateTime.now());

        // When
        Lecture lecture = Lecture.of(1L, "spring", 3, 30, LocalDateTime.now());
        when(loadLecturePort.loadByIdAndIncrementApplicantCount(anyLong())).thenReturn(lecture);
        when(loadLectureHistoryPort.exitsByLectureIdAndUserId(anyLong(), anyLong())).thenReturn(true);

        // Then
        assertThatThrownBy(() -> applyLectureService.apply(command))
                .isInstanceOf(AlreadyApplyException.class)
                .hasMessage(ReturnMessage.ALREADY_SIGNED_UP_FOR_LECTURE.getMessage());
    }

    @DisplayName("[강의를 신청하지 않은 경우] 강의 신청 내역 테이블에 데이터가 없는 경우 예외 발생하지 않음")
    @Test
    void givenLectureIdAndUserId_whenLectureHistoryDoesNotExists_thenNoException(){
        // Given
        Long lectureId = 1L;
        Long userId = 1L;

        // When
        when(loadLectureHistoryPort.exitsByLectureIdAndUserId(lectureId, userId)).thenReturn(false);

        // Then
        assertDoesNotThrow(() -> {
            applyLectureService.checkAlreadyApplied(lectureId, userId);
        });
    }

    @DisplayName("[이미 신청한 강의] 강의 신청 내역 테이블에 데이터가 있는 경우 예외발생")
    @Test
    void givenLectureIdAndUserId_whenLectureHistoryDoesExists_thenException(){
        // Given
        Long lectureId = 1L;
        Long userId = 1L;

        // When
        when(loadLectureHistoryPort.exitsByLectureIdAndUserId(lectureId, userId)).thenReturn(true);

        // Then
        assertThatThrownBy(() -> applyLectureService.checkAlreadyApplied(lectureId, userId))
                .isInstanceOf(AlreadyApplyException.class)
                .hasMessage(ReturnMessage.ALREADY_SIGNED_UP_FOR_LECTURE.getMessage());

    }

    @DisplayName("강의 조회시 신청 가능한 인원을 증가후 반환")
    @Test
    void givenLectureId_whenIncrementApplicantCount_thenReturnLecture(){
        // Given
        Long lectureId = 1L;

        // When
        when(loadLecturePort.loadByIdAndIncrementApplicantCount(any())).thenReturn(Lecture.of(1L, "spring",1,30, LocalDateTime.now()));

        // Then
        Lecture lecture = applyLectureService.findLectureAndIncrementApplicantCount(lectureId);
        assertThat(lecture.getApplicantCount()).isEqualTo(1);
    }

    @DisplayName("강의 내역 테이블에 강의 신청 내역 저장")
    @Test
    void givenLectureIdAndUserId_whenWriteLectureHistory_thenSaveLectureHistory(){
        //Given
        Long lectureId = 1L;
        Long userId = 1L;

        //When
        applyLectureService.writeLectureHistory(lectureId, userId);

        //Then
        verify(writeLectureHistoryPort).save(
                LectureHistory.of(lectureId, userId, any())
        );
    }

    @DisplayName("강의 정원이 가득 차지 않은 경우 예외가 발생하지 않음")
    @Test
    void givenLecture_whenApplicationPossible_thenNotException(){
        // Given
        when(lecture.isApplicationPossible()).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> {
            applyLectureService.applyApplicationForLecture(lecture);
        });
    }

    @DisplayName("강의 정원이 가득 찬 경우 예외가 발생")
    @Test
    void givenLecture_whenApplicationPossible_thenException(){
        // Given
        when(lecture.isApplicationPossible()).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> applyLectureService.applyApplicationForLecture(lecture))
                .isInstanceOf(LectureApplicationFullException.class)
                .hasMessage(ReturnMessage.LECTURE_FULL.getMessage());
    }

}