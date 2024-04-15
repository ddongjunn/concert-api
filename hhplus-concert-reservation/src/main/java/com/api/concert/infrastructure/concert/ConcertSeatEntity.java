package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.ConcertSeat;
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

    private Long concertOptionId;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private int seatNo;

    private int price;

    @Builder
    public ConcertSeatEntity(Long seatId, Long concertOptionId, SeatStatus status, int seatNo, int price) {
        this.seatId = seatId;
        this.concertOptionId = concertOptionId;
        this.status = status;
        this.seatNo = seatNo;
        this.price = price;
    }

    public static ConcertSeat toDomain(ConcertSeatEntity concertSeatEntity){
        return ConcertSeat.builder()
                .seatId(concertSeatEntity.getSeatId())
                .concertOptionId(concertSeatEntity.getConcertOptionId())
                .status(concertSeatEntity.getStatus())
                .seatNo(concertSeatEntity.getSeatNo())
                .price(concertSeatEntity.getPrice())
                .build();
    }
}
