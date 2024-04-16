package com.api.concert.domain.concert;

import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.controller.concert.dto.ConcertTempReservationRequest;
import com.api.concert.controller.concert.dto.ConcertTempReservationResponse;
import com.api.concert.domain.concert.constant.SeatStatus;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConcertSeatService {

    private final int SEAT_LIMIT = 50;
    private final int SEAT_TEMP_TIME = 1;
    private final IConcertSeatRepository iConcertSeatRepository;

    public ConcertSeatResponse findAvailableConcertSeat(Long concertOptionId) {
        //status available 제외 하고 불러옴
        List<ConcertSeat> reservedSeats = iConcertSeatRepository.findReservedSeats(concertOptionId);
        assertSeatsAvailable(reservedSeats);

        List<ConcertSeat> concertSeats = findAvailableSeats(reservedSeats, SEAT_LIMIT);
        return ConcertSeat.toSeatResponse(
                concertOptionId,
                concertSeats
        );
    }

    public void assertSeatsAvailable(List<ConcertSeat> reservedSeats) {
        if(reservedSeats.size() == SEAT_LIMIT){
            throw new CommonException(ResponseCode.NO_SEATS_AVAILABLE, ResponseCode.NO_SEATS_AVAILABLE.getMessage());
        }
    }

    public List<ConcertSeat> findAvailableSeats(List<ConcertSeat> reservedSeats, int SEAT_LIMIT) {
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

    public ConcertTempReservationResponse temporaryReservationSeat(ConcertTempReservationRequest request){
        Long concertOptionId = request.concertOptionId();
        Long userId = request.userId();
        int seatNo = request.seatNo();
        checkSeatNo(seatNo);

        ConcertSeat concertSeat = findSeat(concertOptionId, seatNo);
        if(concertSeat == null){
            ConcertSeat savedConcertSeat = createConcertSeatForTemporaryReservation(concertOptionId, userId, seatNo);
            return ConcertSeat.toTempReservationResponse(LocalDateTime.now().plusMinutes(SEAT_TEMP_TIME));
        }

        if(concertSeat.getStatus() == SeatStatus.AVAILABLE){
            ConcertSeat updatedConcertSeat = updateConcertSeatTemporaryReservation(concertSeat, userId);
            return ConcertSeat.toTempReservationResponse(LocalDateTime.now().plusMinutes(SEAT_TEMP_TIME));
        }

        //ConcertStatus == RESERVED 인 경우
        throw new CommonException(ResponseCode.ALREADY_RESERVED_SEAT, ResponseCode.ALREADY_RESERVED_SEAT.getMessage());
    }

    public ConcertSeat createConcertSeatForTemporaryReservation(Long concertOptionId, Long userId, int seatNo) {
        return iConcertSeatRepository.save(
                ConcertSeat.toEntity(
                        ConcertSeat.builder()
                                .concertOptionId(concertOptionId)
                                .userId(userId)
                                .seatNo(seatNo)
                                .status(SeatStatus.TEMPORARY)
                                .price(getSeatPrice(seatNo))
                                .build()
                )
        );
    }

    public ConcertSeat updateConcertSeatTemporaryReservation(ConcertSeat concertSeat, Long userId) {
        concertSeat.updateStatusAndUserId(SeatStatus.TEMPORARY, userId);
        return iConcertSeatRepository.save(
                ConcertSeat.toEntity(
                        concertSeat
                )
        );
    }

    public ConcertSeat findSeat(Long concertOptionId, int seatNo) {
        return iConcertSeatRepository.findByConcertOptionIdAndSeatNo(concertOptionId, seatNo);
    }

    public int getSeatPrice(int seatNumber){
        return ((seatNumber - 1) / 10 + 1) * 1000;
    }

    public void checkSeatNo(int seatNo) {
        if(1 > seatNo || seatNo > SEAT_LIMIT){
            throw new CommonException(ResponseCode.NOT_EXIST_SEAT, ResponseCode.NOT_EXIST_SEAT.getMessage());
        }
    }
}
