package com.api.concert.application;

import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.controller.point.dto.PointChargeResponse;
import com.api.concert.controller.point.dto.PointResponse;
import com.api.concert.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PointFacade {

    private final PointService pointService;

    public PointChargeResponse charge(PointChargeRequest pointChargeRequest) {
        return pointService.charge(pointChargeRequest);
    }

    public PointResponse find(Long userId) {
        return pointService.findPoint(userId);
    }
}
