package com.api.concert.controller.point;

import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.controller.point.dto.PointChargeResponse;
import com.api.concert.controller.point.dto.PointResponse;
import com.api.concert.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PointController {

    private final PointService pointService;

    @PatchMapping("/point")
    public PointChargeResponse patchPoint(@RequestBody PointChargeRequest pointChargeRequest){
        return pointService.charge(pointChargeRequest);
    }

    @GetMapping("/point/{userId}")
    public PointResponse getPoint(@PathVariable Long userId){
        return pointService.findPoint(userId);
    }
}
