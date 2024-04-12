package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.constant.SeatStatus;
import com.api.concert.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "concert_seat")
public class ConcertSeatEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    private Long concert_option_id;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private int seatNo;

    private int price;

    @Builder
    public ConcertSeatEntity(Long seatId, Long concert_option_id, SeatStatus status, int seatNo, int price) {
        this.seatId = seatId;
        this.concert_option_id = concert_option_id;
        this.status = status;
        this.seatNo = seatNo;
        this.price = price;
    }
}
