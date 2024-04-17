package com.api.concert.domain.concert;

import com.api.concert.controller.concert.dto.ConcertSeatResponse;
import com.api.concert.global.common.exception.CommonException;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.concert.projection.ConcertInfo;
import com.api.concert.infrastructure.concert.projection.ReservationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConcertService {
    private final IConcertOptionRepository iConcertOptionRepository;

    public List<ConcertInfo> findAvailableConcerts() {
        List<ConcertInfo> reservedConcerts = iConcertOptionRepository.availableConcerts();
        checkConcertAvailability(reservedConcerts);

        return reservedConcerts;
    }

    public void checkConcertAvailability(List<ConcertInfo> reservedConcerts) {
        if(reservedConcerts.isEmpty()){
            throw new CommonException(ResponseCode.NO_CONCERT_AVAILABLE, ResponseCode.NO_CONCERT_AVAILABLE.getMessage());
        }
    }

    public void checkConcertOptionId(Long concertOptionId) {
        boolean isExist = iConcertOptionRepository.existFindById(concertOptionId);
        if(!isExist){
            throw new CommonException(ResponseCode.NOT_EXIST_CONCERT, ResponseCode.NOT_EXIST_CONCERT.getMessage());
        }
    }

    public ReservationInfo findConcertInformation(Long concertOptionId) {
        return iConcertOptionRepository.findConcertInformation(concertOptionId);
    }
}
