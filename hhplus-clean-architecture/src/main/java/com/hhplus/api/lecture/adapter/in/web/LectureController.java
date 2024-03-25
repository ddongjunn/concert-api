package com.hhplus.api.lecture.adapter.in.web;

import com.hhplus.api.lecture.application.port.in.ApplyLectureCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public class LectureController {

    @PostMapping("/lectures/{lectureId}/apply/{userId}")
    public ResponseEntity<?> applyLecture(
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("userId") Long userId
    ){
        ApplyLectureCommand command = new ApplyLectureCommand(
                lectureId,
                userId);

        return null;
    }

    @GetMapping("/lectures/{lectureId}/check/{userId}")
    public ResponseEntity<?> checkLecture(
            @PathVariable("lectureId") Long lectureId,
            @PathVariable("userId") Long userId
    ){
        return null;
    }
}
