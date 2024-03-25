package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.database.PointHistoryTable;
import io.hhplus.tdd.point.database.UserPointTable;
import io.hhplus.tdd.point.dto.PointDto;
import io.hhplus.tdd.point.error.ErrorCode;
import io.hhplus.tdd.point.domain.constant.TransactionType;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public synchronized UserPoint chargePoint(PointDto pointDto) throws Exception {
        UserPoint userPoint = userPointTable.selectById(pointDto.userId())
                .charge(pointDto.amount());

        pointHistoryTable.insert(pointDto.userId(), pointDto.amount(), TransactionType.CHARGE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }

    public UserPoint checkPoint(Long userId) throws InterruptedException {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> checkPointHistory(Long userId){
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public synchronized UserPoint usePoint(PointDto pointDto) throws Exception {
        UserPoint userPoint = userPointTable.selectById(pointDto.userId())
                .use(pointDto.amount());
        pointHistoryTable.insert(pointDto.userId(), pointDto.amount(), TransactionType.USE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }
}
