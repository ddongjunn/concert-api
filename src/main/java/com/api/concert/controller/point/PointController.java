package com.api.concert.controller.point;

import com.api.concert.application.PointFacade;
import com.api.concert.controller.point.dto.PointChargeRequest;
import com.api.concert.controller.point.dto.PointChargeResponse;
import com.api.concert.controller.point.dto.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PointController {

    private final PointFacade pointFacade;

    @PatchMapping("/point")
    public PointChargeResponse patchPoint(@RequestBody PointChargeRequest pointChargeRequest){
        return pointFacade.charge(pointChargeRequest);
    }

    @GetMapping("/point/{userId}")
    public PointResponse getPoint(@PathVariable Long userId){
        return pointFacade.find(userId);
    }
}
