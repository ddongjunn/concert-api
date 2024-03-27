package com.hhplus.api.lecture.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LectureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column String name;
    @Column int applicantCount;
    @Column int capacityLimit;
    @Column LocalDateTime startDate;

    public void incrementApplicantCount(){
        this.applicantCount++;
    }

    public void decrementApplicantCount() {
        this.applicantCount--;
    }
}
