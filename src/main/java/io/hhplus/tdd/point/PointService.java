package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PointService {

    @Autowired
    UserPointTable userPointTable;

    @Autowired
    PointHistoryTable pointHistoryTable;

    private static long MAX_POINT = 10000;

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    //point검색
    public UserPoint detail(long id) {
        log.info("point detail service start ======");

        UserPoint userPoint = userPointTable.selectById(id);

        log.info("point detail service end =======");
        //return userPointTable.selectById(id);
        return userPoint;
    }

    //point충전
    public UserPoint charge(long id, long amount) {
        log.info("point charge service start ======");
        if(amount < 0) {
            throw new IllegalArgumentException("충전 포인트는 0보다 작을 수 없습니다.");
        }
        //user검색 null일시 0으로 검색됨
        UserPoint userPoint = userPointTable.selectById(id);

        if(userPoint.point() + amount > MAX_POINT) {
            throw new IllegalArgumentException("포인트 충전은 "+MAX_POINT+"까지 가능합니다.");
        }
        //pointTable 저장
        UserPoint putUserData = userPointTable.insertOrUpdate(userPoint.id(),  userPoint.point() + amount);
        //history저장
        if(putUserData != null) {
            log.info("User Point save success =======");
            PointHistory saveHistory = pointHistoryTable.insert(putUserData.id(), amount, TransactionType.CHARGE, putUserData.updateMillis());
            if(saveHistory != null) {
                log.info("Point History save success =======");
            }
        }else{
            throw new IllegalStateException("포인트 저장에 실패했습니다.");
        }
        return putUserData;
    }

    public UserPoint use(long id, long amount){
        log.info("point use service start ======");
        //user검색 null일시 0으로 검색됨
        UserPoint userPoint = userPointTable.selectById(id);

        if(userPoint.point() - amount < 0) {
            throw new IllegalArgumentException("사용 포인트는 0보다 작을 수 없습니다.");
        }

        //pointTable 저장
        UserPoint putUserData = userPointTable.insertOrUpdate(userPoint.id(),  userPoint.point() - amount);
        if(putUserData != null) {
            log.info("User Point save success =======");
            PointHistory saveHistory = pointHistoryTable.insert(putUserData.id(), amount, TransactionType.USE, putUserData.updateMillis());
            if(saveHistory != null) {
                log.info("Point History save success =======");
            }
        }else{
            throw new IllegalStateException("포인트 저장에 실패했습니다.");
        }
        log.info("point use service end ======");

        return putUserData;
    }


    public List<PointHistory> histoty(long id) {
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);

        if(!pointHistories.isEmpty()){
            return pointHistories;
        }else{
            throw new IllegalArgumentException("포인트 내역이 존재하지 않습니다.");
        }
    }
}