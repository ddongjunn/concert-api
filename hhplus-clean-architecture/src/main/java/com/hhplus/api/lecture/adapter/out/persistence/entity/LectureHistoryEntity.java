package com.hhplus.api.lecture.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long lectureId;

    Long userId;

    LocalDateTime registerDate;

    public LectureHistoryEntity(Long lectureId, Long userId, LocalDateTime registerDate){
        this.lectureId = lectureId;
        this.userId = userId;
        this.registerDate = registerDate;
    }

}
