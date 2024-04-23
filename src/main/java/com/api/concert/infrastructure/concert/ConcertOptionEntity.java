package com.api.concert.infrastructure.concert;

import com.api.concert.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "concert_option")
public class ConcertOptionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long concertOptionId;

    private Long concertId;

    private String venue;

    private LocalDateTime reservationStartDate;

    private LocalDateTime startDate;
}
