package com.hhplus.api.lecture.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Lecture {
    Long id;
    String name;
    int applicantCount;
    int capacityLimit;
    LocalDateTime startDate;

    public static Lecture of(Long id, String name, int applicantCount, int capacityLimit, LocalDateTime startDate) {
        return new Lecture(id, name, applicantCount, capacityLimit, startDate);
    }

    public boolean isApplicationPossible(){
        return this.applicantCount < this.capacityLimit;
    }

    public void incrementApplicantCount(){
        this.applicantCount++;
    }
}
