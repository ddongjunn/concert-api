package com.api.concert.domain.concert;

import com.api.concert.controller.concert.dto.ConcertSeatDTO;
import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.domain.concert.constant.SeatStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@ToString
@NoArgsConstructor
public class ConcertSeat {

    private Long seatId;
    private Long concertOptionId;

    private SeatStatus status;

    private int seatNo;
    private int price;


    @Builder
    public ConcertSeat(Long seatId, Long concertOptionId, int seatNo, int price, SeatStatus status) {
        this.seatId = seatId;
        this.concertOptionId = concertOptionId;
        this.seatNo = seatNo;
        this.price = price;
        this.status = status;
    }

    public static List<ConcertSeat> checkAvailableSeats(List<ConcertSeat> reservedSeats) {
        final int SEAT_LIMIT = 50;
        Set<Integer> reservedSeatNumbers = reservedSeats.stream()
                .map(ConcertSeat::getSeatNo)
                .collect(Collectors.toSet());

        return IntStream.rangeClosed(1, SEAT_LIMIT)
                .filter(i -> !reservedSeatNumbers.contains(i))
                .mapToObj(i -> ConcertSeat.builder()
                        .seatNo(i)
                        .price(getSeatPrice(i))
                        .build())
                .toList();
    }

    public static int getSeatPrice(int seatNumber){
        return ((seatNumber - 1) / 10 + 1) * 1000;
    }

    public static ConcertSeatResponse toResponse(Long concertOptionId, List<ConcertSeat> availableReservedSeats){
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
}
