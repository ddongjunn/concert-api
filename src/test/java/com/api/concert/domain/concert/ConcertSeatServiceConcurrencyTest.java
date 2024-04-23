package com.api.concert.domain.concert;

import com.api.concert.controller.concert.dto.ConcertTempReservationRequest;
import com.api.concert.domain.concert.constant.SeatStatus;
import com.api.concert.infrastructure.concert.ConcertSeatEntity;
import com.api.concert.infrastructure.concert.ConcertSeatJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ConcertSeatServiceConcurrencyTest {

    private final ConcertSeatService concertSeatService;
    private final ConcertSeatJpaRepository concertSeatJpaRepository;

    @Autowired
    public ConcertSeatServiceConcurrencyTest(ConcertSeatService concertSeatService, ConcertSeatJpaRepository concertSeatJpaRepository) {
        this.concertSeatService = concertSeatService;
        this.concertSeatJpaRepository = concertSeatJpaRepository;
    }

    /**
     * 좌석에 대한 ROW가 생성되는 시점은 임시 상태로 1번이라도 예약이 되었던 경우
     */
    @DisplayName("[좌석에 대한 ROW가 없는 경우] - 동시에 여러명이 한 좌석을 예약하는 경우 1명만 예약이 되어야 한다.")
    @Test
    void test_temporaryReservationSeat_notRow() throws InterruptedException {
        // Given
        Long concertOptionId = 2L;
        int seatNo = 1;

        // When
        int numberOfRequest = 10;
        CountDownLatch latch = new CountDownLatch(numberOfRequest);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequest);

        for(int i = 0; i < numberOfRequest; i++){
            Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    ConcertTempReservationRequest concertTempReservationRequest = createConcertTempReservationRequest(concertOptionId, userId, seatNo);
                    concertSeatService.temporaryReservationSeat(concertTempReservationRequest);
                } catch (Exception e){
                    log.error("exception {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        ConcertSeatEntity result = concertSeatJpaRepository.findByConcertOptionIdAndSeatNo(concertOptionId, seatNo);
        assertThat(result.getStatus()).isEqualTo(SeatStatus.TEMPORARY);
    }

    @DisplayName("[좌석에 대한 ROW가 있는 경우] - 동시에 여러명이 한 좌석을 예약하는 경우 처음에 진입한 사용자가 예약 성공한다.")
    @Test
    void test() throws InterruptedException {
        // Given
        Long concertOptionId = 3L;
        int seatNo = 1;
        ConcertSeatEntity concertSeatEntity = ConcertSeatEntity.builder().concertOptionId(concertOptionId).seatNo(seatNo).status(SeatStatus.AVAILABLE).build();
        concertSeatJpaRepository.save(concertSeatEntity);
        Queue<Long> reservationOrderQueue = new ArrayDeque<>();

        // When
        int numberOfRequest = 10;
        CountDownLatch latch = new CountDownLatch(numberOfRequest);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequest);

        for(int i = 1; i <= numberOfRequest; i++){
            Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    ConcertTempReservationRequest concertTempReservationRequest = createConcertTempReservationRequest(concertOptionId, userId, seatNo);
                    concertSeatService.temporaryReservationSeat(concertTempReservationRequest);
                    reservationOrderQueue.offer(userId);
                } catch (Exception e){
                    log.error("exception {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        ConcertSeatEntity result = concertSeatJpaRepository.findByConcertOptionIdAndSeatNo(concertOptionId, seatNo);
        assertThat(result.getUserId()).isEqualTo(reservationOrderQueue.poll());
        assertThat(result.getStatus()).isEqualTo(SeatStatus.TEMPORARY);
    }

    public ConcertTempReservationRequest createConcertTempReservationRequest(
            Long concertOptionId,
            Long userId,
            int seatNo)
    {
            return ConcertTempReservationRequest.builder()
                    .concertOptionId(concertOptionId)
                    .userId(userId)
                    .seatNo(seatNo)
                    .build();
    }
}
