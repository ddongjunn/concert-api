package com.api.concert.infrastructure.concert;

import com.api.concert.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reservation_history")
public class ReservationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;

    String name;

    String singer;

    int seatNo;

    int price;

    LocalDateTime StartAt;
}
