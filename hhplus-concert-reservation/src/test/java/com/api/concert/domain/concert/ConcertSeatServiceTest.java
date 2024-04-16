package com.api.concert.domain.concert;

import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.controller.concert.dto.ConcertTempReservationRequest;
import com.api.concert.controller.concert.dto.ConcertTempReservationResponse;
import com.api.concert.domain.concert.constant.SeatStatus;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.concert.ConcertSeatEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertSeatServiceTest {

    @Mock
    IConcertSeatRepository iConcertSeatRepository;

    @InjectMocks
    ConcertSeatService concertSeatService;

    @DisplayName("예약 가능한 좌석 조회")
    @Test
    void test_findAvailableConcertSeat(){
        // Given
        Long concertOptionId = 1L;
        List<ConcertSeat> reservedSeats = makeReservedSeats(6);

        // When
        when(iConcertSeatRepository.findReservedSeats(any())).thenReturn(reservedSeats);
        ConcertSeatResponse availableConcertSeat = concertSeatService.findAvailableConcertSeat(concertOptionId);

        // Then
        assertThat(availableConcertSeat.seats()).hasSize(44);
    }

    @DisplayName("예약 가능한 좌석이 없는 경우")
    @Test
    void test_findAvailableConcertSeat_notExist(){
        // Given
        Long concertOptionId = 1L;
        List<ConcertSeat> reservedSeats = makeReservedSeats(50);

        // When & Then
        when(iConcertSeatRepository.findReservedSeats(any())).thenReturn(reservedSeats);
        assertThatThrownBy(() -> concertSeatService.findAvailableConcertSeat(concertOptionId))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.NO_SEATS_AVAILABLE)
                .hasMessage(ResponseCode.NO_SEATS_AVAILABLE.getMessage());
    }

    List<ConcertSeat> makeReservedSeats(int size){
        return IntStream.rangeClosed(1, size)
                .mapToObj(i -> ConcertSeat.builder().seatNo(i).build())
                .toList();
    }

    @DisplayName("[좌석 임시 예약] - 이미 예약된 좌석인 경우 Exception")
    @Test
    void test_temporaryReservationSeat_alreadyReserved(){
        // Given
        Long concertOptionId = 1L;
        Long userId = 1L;
        int seatNo = 10;
        ConcertTempReservationRequest request = createRequest(concertOptionId, userId, seatNo);

        // When & Then
        ConcertSeat concertSeat = createConcertSeatToStatus(SeatStatus.REVERSED);
        when(iConcertSeatRepository.findByConcertOptionIdAndSeatNo(anyLong(), anyInt()))
                .thenReturn(concertSeat);

        assertThatThrownBy(() -> concertSeatService.temporaryReservationSeat(request))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode", ResponseCode.ALREADY_RESERVED_SEAT)
                .hasMessage(ResponseCode.ALREADY_RESERVED_SEAT.getMessage());
    }

    @DisplayName("[좌석 임시 예약] - 좌석 정보가 null 인 경우")
    @Test
    void test_temporaryReservationSeat_null(){
        // Given
        Long concertOptionId = 1L;
        Long userId = 1L;
        int seatNo = 10;
        ConcertTempReservationRequest request = createRequest(concertOptionId, userId, seatNo);

        // When
        ConcertSeat concertSeat = null;
        when(iConcertSeatRepository.findByConcertOptionIdAndSeatNo(anyLong(), anyInt())).thenReturn(null);

        ConcertSeat savedConcertSeat = createConcertSeatToStatus(SeatStatus.TEMPORARY);
        when(iConcertSeatRepository.save(any(ConcertSeatEntity.class))).thenReturn(savedConcertSeat);

        //Then
        ConcertTempReservationResponse result = concertSeatService.temporaryReservationSeat(request);
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo(ResponseCode.SUCCESS);
    }

    @DisplayName("[좌석 임시 예약] - 좌석 정보 상태가 AVAILABLE 경우")
    @Test
    void test_temporaryReservationSeat_available(){
        // Given
        Long concertOptionId = 1L;
        Long userId = 1L;
        int seatNo = 10;
        ConcertTempReservationRequest request = createRequest(concertOptionId, userId, seatNo);

        // When
        ConcertSeat concertSeat = createConcertSeatToStatus(SeatStatus.AVAILABLE);
        when(iConcertSeatRepository.findByConcertOptionIdAndSeatNo(anyLong(), anyInt())).thenReturn(null);

        ConcertSeat savedConcertSeat = createConcertSeatToStatus(SeatStatus.TEMPORARY);
        when(iConcertSeatRepository.save(any(ConcertSeatEntity.class))).thenReturn(savedConcertSeat);

        //Then
        ConcertTempReservationResponse result = concertSeatService.temporaryReservationSeat(request);
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo(ResponseCode.SUCCESS);
    }

    @DisplayName("존재 하지 않는 좌석번호")
    @Test
    void test_checkSeatNo(){
        //Given
        int seatNo = 51;

        //When & Then
        assertThatThrownBy(() -> concertSeatService.checkSeatNo(seatNo))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("ResponseCode",ResponseCode.NOT_EXIST_SEAT)
                .hasMessage(ResponseCode.NOT_EXIST_SEAT.getMessage());
    }

    @DisplayName("예약 가능한 좌석")
    @Test
    void test_checkSeatAvailability(){
        // Given
        Long concertOptionId = 1L;
        int seatNo = 10;
        ConcertSeat concertSeat = ConcertSeat.builder().concertOptionId(concertOptionId).seatNo(seatNo).status(SeatStatus.AVAILABLE).build();

        // When
        when(iConcertSeatRepository.findByConcertOptionIdAndSeatNo(concertOptionId, seatNo)).thenReturn(concertSeat);

        // Then
    }

    @DisplayName("이미 예약 된 좌석인 경우 Exception")
    @Test
    void test_checkSeatAvailability_alreadyReservation(){
        // Given
        Long concertOptionId = 1L;
        Long userId = 1L;
        int seatNo = 10;
        ConcertTempReservationRequest request = createRequest(concertOptionId, userId, seatNo);
        ConcertSeat concertSeat = ConcertSeat.builder().concertOptionId(concertOptionId).userId(userId).seatNo(seatNo).status(SeatStatus.REVERSED).build();

        // When
        when(iConcertSeatRepository.findByConcertOptionIdAndSeatNo(concertOptionId, seatNo)).thenReturn(concertSeat);

        // Then
        assertThatThrownBy(() -> concertSeatService.temporaryReservationSeat(request))
                .isInstanceOf(CommonException.class)
                .hasFieldOrPropertyWithValue("responseCode", ResponseCode.ALREADY_RESERVED_SEAT)
                .hasMessage(ResponseCode.ALREADY_RESERVED_SEAT.getMessage());
    }

    ConcertTempReservationRequest createRequest(Long concertOptionId, Long userId, int seatNo){
        return ConcertTempReservationRequest.builder()
                .concertOptionId(concertOptionId)
                .userId(userId)
                .seatNo(seatNo)
                .build();
    }

    ConcertSeat createConcertSeatToStatus(SeatStatus status){
        return ConcertSeat.builder()
                .seatId(1L)
                .concertOptionId(1L)
                .userId(1L)
                .status(status)
                .seatNo(10)
                .price(1000)
                .build();
    }
}