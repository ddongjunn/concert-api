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

    public UserPoint chargePoint(Long userId, Long amount) throws Exception {
        if(amount < 0){
            throw new RuntimeException(ErrorCode.INCORRECT_AMOUNT.getCode());
        }

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

    public synchronized UserPoint usePoint(Long userId, Long amount) throws Exception {
        UserPoint userPoint = userPointTable.selectById(userId);
        if(userPoint.point() - amount < 0){
            throw new Exception("사용할 수 있는 포인트가 부족합니다. 현재 포인트 : " + userPoint.point());
        }

        pointHistoryTable.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userId, userPoint.point() - amount);
    }
}
