package com.api.concert.infrastructure.concert;

import com.api.concert.domain.concert.ConcertSeat;
import com.api.concert.domain.concert.constant.SeatStatus;
import com.api.concert.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "concert_seat",
        uniqueConstraints = {
            @UniqueConstraint(name = "concertOptionId_seatNo",
            columnNames = {"concertOptionId", "seatNo"}
            )
        }
    )
public class ConcertSeatEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    private Long concertOptionId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private int seatNo;

    private int price;

    @Builder
    public ConcertSeatEntity(Long seatId, Long concertOptionId, Long userId, SeatStatus status, int seatNo, int price, LocalDateTime updatedAt) {
        this.seatId = seatId;
        this.concertOptionId = concertOptionId;
        this.userId = userId;
        this.status = status;
        this.seatNo = seatNo;
        this.price = price;
        this.updatedAt = updatedAt;
    }

    public static ConcertSeat toDomain(ConcertSeatEntity concertSeatEntity){
        return ConcertSeat.builder()
                .seatId(concertSeatEntity.getSeatId())
                .concertOptionId(concertSeatEntity.getConcertOptionId())
                .userId(concertSeatEntity.getUserId())
                .status(concertSeatEntity.getStatus())
                .seatNo(concertSeatEntity.getSeatNo())
                .price(concertSeatEntity.getPrice())
                .updatedAt(concertSeatEntity.getUpdatedAt())
                .build();
    }
}
