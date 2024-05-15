package com.api.concert.domain.concert;

import com.api.concert.common.exception.CommonException;
import com.api.concert.common.model.ResponseCode;
import com.api.concert.infrastructure.concert.projection.ConcertInfoProjection;
import com.api.concert.infrastructure.concert.projection.ReservationInfoProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConcertService {
    private final IConcertOptionRepository iConcertOptionRepository;

    public List<ConcertInfoProjection> findAvailableConcerts() {
        List<ConcertInfoProjection> reservedConcerts = iConcertOptionRepository.availableConcerts();
        checkConcertAvailability(reservedConcerts);

        return reservedConcerts;
    }

    public void checkConcertAvailability(List<ConcertInfoProjection> reservedConcerts) {
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

    public ReservationInfoProjection findConcertInformation(Long concertOptionId) {
        return iConcertOptionRepository.findConcertInformation(concertOptionId);
    }
}
