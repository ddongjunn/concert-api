package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint pointCharge(Long userId, Long amount) throws InterruptedException {
        UserPoint userPoint = userPointTable.selectById(userId);
        return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point() + amount);
    }

    public UserPoint pointCheck(Long userId) throws InterruptedException {
        return userPointTable.selectById(userId);
    }
}
