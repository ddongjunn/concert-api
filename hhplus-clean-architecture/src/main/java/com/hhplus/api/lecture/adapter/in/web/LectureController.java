package com.hhplus.api.lecture.adapter.in.web;

import com.hhplus.api.common.ResponseMessage;
import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureStatusCommand;
import com.hhplus.api.lecture.application.port.in.ApplyLectureStatusUseCase;
import com.hhplus.api.lecture.application.port.in.ApplyLectureUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LectureController {

    private final ApplyLectureUseCase applyLectureUseCase;
    private final ApplyLectureStatusUseCase applyLectureStatusUseCase;

    @PostMapping("/lectures/{lectureId}/apply/{userId}")
    public ResponseEntity<?> applyLecture(
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("userId") Long userId
    ){
        return new ResponseEntity<>(applyLectureUseCase.apply(
                new ApplyLectureCommand(
                lectureId,
                userId)
        ), HttpStatus.OK);
    }

    @GetMapping("/lectures/{lectureId}/check/{userId}")
    public ResponseEntity<?> checkLecture(
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("userId") Long userId
    ){
        return new ResponseEntity<>(applyLectureStatusUseCase.applyStatus(
                new ApplyLectureStatusCommand(
                        lectureId,
                        userId
                )
        ), HttpStatus.OK);
    }
}
