package com.hhplus.api.lecture.adapter.in.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(staticName = "of")
public class LectureDetail {

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
