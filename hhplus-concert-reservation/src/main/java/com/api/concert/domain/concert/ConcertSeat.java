package com.api.concert.domain.concert;

import com.api.concert.controller.concert.dto.ConcertSeatDTO;
import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.controller.concert.dto.ConcertTempReservationResponse;
import com.api.concert.domain.concert.constant.SeatStatus;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.concert.ConcertSeatEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@ToString
@NoArgsConstructor
public class ConcertSeat {

    private Long seatId;
    private Long concertOptionId;
    private Long userId;

    private SeatStatus status;

    private int seatNo;
    private int price;
    private LocalDateTime updatedAt;


    @Builder
    public ConcertSeat(Long seatId, Long concertOptionId, Long userId, int seatNo, int price, SeatStatus status, LocalDateTime updatedAt) {
        this.seatId = seatId;
        this.concertOptionId = concertOptionId;
        this.userId = userId;
        this.seatNo = seatNo;
        this.price = price;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public void updateStatusAndUserId(SeatStatus status, Long userId) {
        this.status = status;
        this.userId = userId;
    }

    public static ConcertSeatResponse toSeatResponse(Long concertOptionId, List<ConcertSeat> availableReservedSeats){
        return ConcertSeatResponse.builder()
                .concertOptionId(concertOptionId)
                .seats(
                        availableReservedSeats.stream()
                                .map(concertSeat ->
                                        ConcertSeatDTO.builder()
                                                .seatNo(concertSeat.seatNo)
                                                .price(concertSeat.price)
                                                .build()
                                ).collect(Collectors.toList())
                ).build();
    }

    public static ConcertTempReservationResponse toTempReservationResponse(LocalDateTime expiredAt){
        String message = String.format("임시 좌석 만료 시간 [%s]", expiredAt);

        return ConcertTempReservationResponse.builder()
                .code(ResponseCode.SUCCESS)
                .message(message)
                .build();
    }

    public static ConcertSeatEntity toEntity(ConcertSeat concertSeat) {
        return ConcertSeatEntity.builder()
                .seatId(concertSeat.getSeatId())
                .concertOptionId(concertSeat.getConcertOptionId())
                .userId(concertSeat.getUserId())
                .status(concertSeat.getStatus())
                .seatNo(concertSeat.getSeatNo())
                .price(concertSeat.getPrice())
                .build();
    }

}