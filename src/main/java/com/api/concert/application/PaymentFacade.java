package com.api.concert.application;

import com.api.concert.common.model.ResponseCode;
import com.api.concert.controller.payment.dto.PaymentRequest;
import com.api.concert.controller.payment.dto.PaymentResponse;
import com.api.concert.controller.point.dto.PointUseRequest;
import com.api.concert.domain.concert.ConcertSeat;
import com.api.concert.domain.concert.ConcertSeatService;
import com.api.concert.domain.concert.Reservation;
import com.api.concert.domain.payment.event.PaymentCompletedEvent;
import com.api.concert.domain.point.PointService;
import com.api.concert.domain.queue.QueueService;
import com.api.concert.modules.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PaymentFacade {

    private final QueueService queueService;
    private final ConcertSeatService concertSeatService;
    private final PointService pointService;

    @Transactional
    public PaymentResponse payment(PaymentRequest paymentRequest) {
        Long userId = paymentRequest.userId();

        //임시 예약된 좌석들 가져오기
        List<ConcertSeat> temporarilyReservedSeats = concertSeatService.findSeatTemporarilyReservedByUserId(userId);
        Long paymentAmount = temporarilyReservedSeats.stream().mapToLong(ConcertSeat::getPrice).sum();

        //예약된 좌석들의 총 금액 결제
        PointUseRequest pointUseRequest = PointUseRequest.builder().userId(userId).point(paymentAmount).build();
        pointService.use(pointUseRequest);

        //좌석 예약처리 -> 예약 내역 반환
        List<Reservation> reservations = concertSeatService.updateSeatToReserved(userId, temporarilyReservedSeats);

        //대기열 삭제
        queueService.expire(userId);

        //TODO 외부 플랫폼 API 호출
        Events.raise(PaymentCompletedEvent.builder().reservations(reservations).build());

        return PaymentResponse.builder()
                .code(ResponseCode.SUCCESS)
                .build();
    }
}
