package com.api.concert.domain.point;

import com.api.concert.controller.point.dto.PointChargeResponse;
import com.api.concert.controller.point.dto.PointResponse;
import com.api.concert.global.common.model.ResponseCode;
import com.api.concert.infrastructure.point.PointEntity;

public class PointConverter {

    public static Point toDomain(PointEntity pointEntity){
        return Point.builder()
                .pointId(pointEntity.getPointId())
                .userId(pointEntity.getUserId())
                .point(pointEntity.getPoint())
                .build();
    }

    public static PointEntity toEntity(Point point){
        return PointEntity.builder()
                .pointId(point.getPointId())
                .userId(point.getUserId())
                .point(point.getPoint())
                .build();
    }

    public static PointChargeResponse toChargeResponse(Point point, Long chargePoint) {
        return PointChargeResponse.builder()
                .responseCode(ResponseCode.SUCCESS)
                .chargePoint(chargePoint)
                .point(point.getPoint())
                .build();
    }

    public static PointResponse toResponse(Point point) {
        return PointResponse.builder()
                .responseCode(ResponseCode.SUCCESS)
                .point(point.getPoint())
                .build();
    }
}
