package com.api.concert.domain.concert;

import com.api.concert.infrastructure.concert.ReservationEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Reservation {
    Long id;

    Long userId;

    String name;

    String singer;

    int seatNo;

    int price;

    LocalDateTime StartAt;

    public static ReservationEntity toEntity(Reservation reservation){
        return ReservationEntity.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .name(reservation.getName())
                .singer(reservation.getSinger())
                .seatNo(reservation.getSeatNo())
                .price(reservation.getPrice())
                .build();
    }
}