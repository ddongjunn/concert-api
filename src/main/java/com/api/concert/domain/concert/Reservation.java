package com.api.concert.domain.concert;

import com.api.concert.infrastructure.concert.ReservationEntity;
import com.api.concert.infrastructure.concert.projection.ReservationInfoProjection;
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

    LocalDateTime startDate;

    public static ReservationEntity toEntity(Reservation reservation){
        return ReservationEntity.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .name(reservation.getName())
                .singer(reservation.getSinger())
                .seatNo(reservation.getSeatNo())
                .price(reservation.getPrice())
                .startDate(reservation.getStartDate())
                .build();
    }

    public static Reservation fromProjection(ReservationInfoProjection projection) {
        return Reservation.builder()
                .userId(projection.getUserId())
                .name(projection.getName())
                .singer(projection.getSinger())
                .seatNo(projection.getSeatNo())
                .price(projection.getPrice())
                .startDate(projection.getStartDate())
                .build();
    }
}