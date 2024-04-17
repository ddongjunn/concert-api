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

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcertSeatServiceTest {

    private final int SEAT_TEMP_TIME = 1;

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
        doNothing().when(iConcertSeatRepository).save(any(ConcertSeatEntity.class));

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
        doNothing().when(iConcertSeatRepository).save(any(ConcertSeatEntity.class));

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

    @DisplayName("[임시 좌석 만료] update_at가 만료시간이 지난 경우 상태 업데이트")
    @Test
    void test_expiredTemporarySeats(){
        // Given
        List<ConcertSeat> expiredTemporarySeats = getExpiredTemporarySeats(10);
        when(iConcertSeatRepository.findExpiredTemporarySeats(any(SeatStatus.class), any(LocalDateTime.class)))
                .thenReturn(expiredTemporarySeats);

        // When
        List<Long> expiredTemporarySeatIds = expiredTemporarySeats.stream().map(ConcertSeat::getSeatId).toList();
        concertSeatService.expireTemporarySeats();

        // Then
        verify(iConcertSeatRepository, times(1)).updateStatusToExpiredBySeatIds(expiredTemporarySeatIds);
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

    List<ConcertSeat> makeReservedSeats(int size){
        return IntStream.rangeClosed(1, size)
                .mapToObj(i -> ConcertSeat.builder().seatNo(i).build())
                .toList();
    }

    public List<ConcertSeat> getExpiredTemporarySeats(int size) {
        return LongStream.rangeClosed(1, size)
                .mapToObj(i -> ConcertSeat.builder()
                        .seatId(i)
                        .seatNo((int) i)
                        .concertOptionId(i)
                        .userId(i)
                        .status(SeatStatus.TEMPORARY)
                        .price(5000)
                        .updatedAt(LocalDateTime.now().minusMinutes(10))
                        .build()
                )
                .toList();
    }
}