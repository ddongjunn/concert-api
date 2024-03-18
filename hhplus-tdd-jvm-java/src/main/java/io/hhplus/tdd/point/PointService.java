package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
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

    public UserPoint chargePoint(Long userId, Long amount) throws InterruptedException {
        UserPoint userPoint = userPointTable.selectById(userId);
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() + amount);
    }

    public UserPoint checkPoint(Long userId) throws InterruptedException {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> checkPointHistory(Long userId){
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
