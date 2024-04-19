package com.api.concert.domain.point;

import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.controller.point.dto.PointChargeResponse;
import com.api.concert.controller.point.dto.PointResponse;
import com.api.concert.controller.point.dto.PointUseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
@Service
public class PointService{

    private final IPointRepository iPointRepository;
    private final IPointHistoryRepository iPointHistoryRepository;

    @Transactional
    public PointChargeResponse charge(PointChargeRequest pointChargeRequest) {
        Long userId = pointChargeRequest.userId();
        Long chargePoint = pointChargeRequest.point();

        Point point = findPointForService(userId);
        point.charge(chargePoint, this::saveHistory);

        return PointConverter.toChargeResponse(
                iPointRepository.updatePoint(PointConverter.toEntity(point)),
                chargePoint
        );
    }

    @Transactional
    public PointChargeResponse use(PointUseRequest pointUseRequest) {
        Long userId = pointUseRequest.userId();
        Long usePoint = pointUseRequest.point();

        Point point = findPointForService(userId);
        point.use(usePoint, this::saveHistory);

        return PointConverter.toChargeResponse(
                iPointRepository.updatePoint(PointConverter.toEntity(point)),
                usePoint
        );
    }

    @Transactional
    public Point findPointForService(Long userId) {
        return iPointRepository.findPointByUserId(userId);
    }

    public PointResponse findPoint(Long userId){
        return PointConverter.toResponse(
                iPointRepository.findPointByUserId(userId)
        );
    }

    public void saveHistory(PointHistory pointHistory){
        iPointHistoryRepository.save(
                PointHistory.toEntity(pointHistory)
        );
    }
}
