package com.api.concert.domain.point;

import com.api.concert.common.annotation.DistributedLock;
import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.controller.point.dto.PointChargeResponse;
import com.api.concert.controller.point.dto.PointResponse;
import com.api.concert.controller.point.dto.PointUseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PointService{

    private final IPointRepository iPointRepository;

    @DistributedLock(key = "'POINT:'.concat('CHARGE:').concat(#pointChargeRequest.userId)")
    public PointChargeResponse charge(PointChargeRequest pointChargeRequest) {
        Long userId = pointChargeRequest.userId();
        Long chargePoint = pointChargeRequest.point();

        Point point = getPoint(userId);
        point.charge(chargePoint);

        return PointConverter.toChargeResponse(
                iPointRepository.updatePoint(PointConverter.toEntity(point)),
                chargePoint
        );
    }

    //@DistributedLock(key = "'POINT:'.concat('USE:').concat(#pointUseRequest.userId)")
    public PointChargeResponse use(PointUseRequest pointUseRequest) {
        Long userId = pointUseRequest.userId();
        Long usePoint = pointUseRequest.point();

        Point point = getPoint(userId);
        point.use(usePoint);

        return PointConverter.toChargeResponse(
                iPointRepository.updatePoint(PointConverter.toEntity(point)),
                usePoint
        );
    }

    public Point getPoint(Long userId) {
        return iPointRepository.findPointByUserId(userId);
    }

    public PointResponse findPoint(Long userId){
        return PointConverter.toResponse(
                iPointRepository.findPointByUserId(userId)
        );
    }
}
