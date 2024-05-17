package com.api.concert.domain.concert;

import com.api.concert.common.event.Events;
import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.controller.concert.dto.ConcertTempReservationRequest;
import com.api.concert.controller.concert.dto.ConcertTempReservationResponse;
import com.api.concert.domain.concert.constant.SeatStatus;
import com.api.concert.common.annotation.DistributedLock;
import com.api.concert.common.exception.CommonException;
import com.api.concert.common.model.ResponseCode;
import com.api.concert.domain.concert.event.ReservationHistoryLoggedEvent;
import com.api.concert.infrastructure.concert.projection.ReservationInfoProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConcertSeatService {

    private final int SEAT_LIMIT = 50;
    private final int SEAT_TEMP_TIME = 1;
    private final IConcertSeatRepository iConcertSeatRepository;

    public ConcertSeatResponse findAvailableSeatsForConcert(Long concertOptionId) {
        //status available 제외 하고 불러옴
        List<ConcertSeat> reservedSeats = iConcertSeatRepository.findReservedSeats(concertOptionId);
        assertSeatsAvailable(reservedSeats);

        List<ConcertSeat> concertSeats = calculateAvailableSeats(reservedSeats, SEAT_LIMIT);
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

    public List<ConcertSeat> calculateAvailableSeats(List<ConcertSeat> reservedSeats, int SEAT_LIMIT) {
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

    @DistributedLock(key = "'SEAT'.concat(#request.seatNo).concat(':CONCERT').concat(#request.concertOptionId)")
    public ConcertTempReservationResponse temporaryReservationSeat(ConcertTempReservationRequest request){
        Long concertOptionId = request.concertOptionId();
        Long userId = request.userId();
        int seatNo = request.seatNo();
        checkSeatNo(seatNo);

        try {
            ConcertSeat concertSeat = findSeat(concertOptionId, seatNo);
            if (concertSeat == null) {
                createConcertSeatForTemporaryReservation(concertOptionId, userId, seatNo);
                return ConcertSeat.toTempReservationResponse(LocalDateTime.now().plusMinutes(SEAT_TEMP_TIME));
            }

            if (concertSeat.getStatus() == SeatStatus.AVAILABLE) {
                updateConcertSeatTemporaryReservation(concertSeat, userId);
                return ConcertSeat.toTempReservationResponse(LocalDateTime.now().plusMinutes(SEAT_TEMP_TIME));
            }

            //ConcertStatus == RESERVED, TEMPORARY 인 경우
            throw new CommonException(ResponseCode.ALREADY_RESERVED_SEAT, ResponseCode.ALREADY_RESERVED_SEAT.getMessage());

        } catch (DataIntegrityViolationException e) {
            if(e.getRootCause() instanceof SQLException) {
                throw new CommonException(ResponseCode.ALREADY_RESERVED_SEAT, ResponseCode.ALREADY_RESERVED_SEAT.getMessage());
            }
        }

        return ConcertTempReservationResponse.builder().build();
    }


    public void createConcertSeatForTemporaryReservation(Long concertOptionId, Long userId, int seatNo) {
        iConcertSeatRepository.save(
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

    public void updateConcertSeatTemporaryReservation(ConcertSeat concertSeat, Long userId) {
        concertSeat.updateStatusAndUserId(SeatStatus.TEMPORARY, userId);
        iConcertSeatRepository.save(
                ConcertSeat.toEntity(
                        concertSeat
                )
        );
    }

    /**
     * 만료 updated_at < LocalDateTime.now().minusMinute(SEAT_TEMP_TIME)
     */
    @Transactional
    @Scheduled(cron = "${seat.scan-time}")
    public void expireTemporarySeats(){
        List<ConcertSeat> expiredTemporarySeats = findExpiredTemporarySeats();
        List<Long> expiredTemporarySeatIds = expiredTemporarySeats.stream()
                .map(ConcertSeat::getSeatId)
                .toList();
        iConcertSeatRepository.updateStatusToExpiredBySeatIds(expiredTemporarySeatIds);
    }

    public List<ConcertSeat> findExpiredTemporarySeats() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(SEAT_TEMP_TIME);
        return iConcertSeatRepository.findExpiredTemporarySeats(SeatStatus.TEMPORARY, expirationTime);
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

    public List<ConcertSeat> findSeatTemporarilyReservedByUserId(Long userId) {
        List<ConcertSeat> temporarilyReservedSeats = iConcertSeatRepository.findTemporarilyReservedSeatsByUserId(userId);

        if(temporarilyReservedSeats.isEmpty()){
            throw new CommonException(ResponseCode.SEAT_UNAVAILABLE, ResponseCode.SEAT_UNAVAILABLE.getMessage());
        }
        return temporarilyReservedSeats;
    }

    @Transactional
    public List<Reservation> updateSeatToReserved(Long userId, List<ConcertSeat> temporarilyReservedSeats) {
        temporarilyReservedSeats.forEach(ConcertSeat::reserve);
        List<Long> seatIds = temporarilyReservedSeats.stream()
                .map(ConcertSeat::getSeatId)
                .toList();
        iConcertSeatRepository.updateStatusToReserved(userId, seatIds);

        List<Reservation> reservations = findReservationInformationByIds(seatIds)
                .stream()
                .map(Reservation::fromProjection)
                .toList();
        saveHistories(reservations);

        return reservations;
    }

    public List<ReservationInfoProjection> findReservationInformationByIds(List<Long> concertSeatIds) {
        return iConcertSeatRepository.findReservationInformationByIds(concertSeatIds);
    }

    public void saveHistories(List<Reservation> reservations) {
        Events.raise(ReservationHistoryLoggedEvent.builder()
                .reservations(reservations)
                .build());
    }
}
