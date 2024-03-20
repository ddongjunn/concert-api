package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.dto.PointRequestDto;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/point")
@RestController
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable Long id) throws InterruptedException {
        return pointService.checkPoint(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable Long id) {
        return pointService.checkPointHistory(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable Long id, @RequestBody PointRequestDto pointRequestDto) throws Exception {
        return pointService.chargePoint(id, pointRequestDto.amount());
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable Long id, @RequestBody PointRequestDto pointRequestDto) throws Exception {
        return pointService.usePoint(id, pointRequestDto.amount());
    }
}
