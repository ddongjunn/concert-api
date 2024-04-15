package com.api.concert.domain.concert;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ConcertSeatTest {

    private ConcertSeat concertSeat;

    @BeforeEach
    void setUp(){
        this.concertSeat = new ConcertSeat();
    }


    @DisplayName("예약 가능한 좌석 조회")
    @Test
    void test_checkAvailableSeats(){
        // Given
        final int SEAT_LIMIT = 50;
        List<ConcertSeat> reservedSeats = Arrays.asList(
                ConcertSeat.builder().seatNo(1).build(),
                ConcertSeat.builder().seatNo(2).build(),
                ConcertSeat.builder().seatNo(15).build(),
                ConcertSeat.builder().seatNo(31).build()
        );

        // When
        List<ConcertSeat> result = ConcertSeat.checkAvailableSeats(reservedSeats);

        // Then
        assertThat(result).hasSize(SEAT_LIMIT - reservedSeats.size());
        result.forEach(
                concertSeat1 -> log.info("seat {}", concertSeat1)
        );
    }

}