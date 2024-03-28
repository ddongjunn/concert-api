package com.hhplus.api.lecture.adapter.in.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hhplus.api.lecture.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LecturesDetailResponse {
    @JsonProperty("정보")
    public LectureResponseHeader header;

    @JsonProperty("강의목록")
    public List<LectureResponseBody> body;

    public LecturesDetailResponse (List<Lecture> lectures) {
        this.header = LectureResponseHeader.of(lectures.size());
        this.body = lectures.stream().map(
                        lecture -> LectureResponseBody.of(lecture.getId(),
                                lecture.getName(),
                                lecture.getApplicantCount(),
                                lecture.getCapacityLimit(),
                                lecture.getStartDate()))
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class LectureResponseHeader {

        @JsonProperty("현재 신청 가능한 강의 수")
        private int lectureCount;
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class LectureResponseBody {

        @JsonProperty("강의 코드")
        private Long id;

        @JsonProperty("강의명")
        private String name;

        @JsonProperty("현재 수강 인원")
        private int applicantCount;

        @JsonProperty("신청 가능 인원")
        private int capacityLimit;

        @JsonProperty("강의 시작일")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime startDate;
    }

}
