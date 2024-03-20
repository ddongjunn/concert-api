package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.error.ErrorCode;
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

    public synchronized UserPoint chargePoint(Long userId, Long amount) throws Exception {
        if(amount < 0){
            throw new RuntimeException(ErrorCode.INCORRECT_AMOUNT.getMessage());
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
            throw new Exception(ErrorCode.INCORRECT_AMOUNT.getMessage(userPoint.point()));
        }

        pointHistoryTable.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());
        return userPointTable.insertOrUpdate(userId, userPoint.point() - amount);
    }
}
