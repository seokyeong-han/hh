package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;

import java.util.List;

//@SpringBootTest //DB가 없어 jdbc오류가 발생하여 주석 처리 mock로 객체를 만들어서 test진행
@Transactional
public class PointIntegrationTest {

    @InjectMocks
    private PointService pointService; // 테스트 대상 클래스

    @Mock
    private UserPointTable userPointTable; // DB 접근을 Mock으로 처리

    @Mock
    private PointHistoryTable pointHistoryTable; // DB 접근을 Mock으로 처리

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
    }


    @Test
    @DisplayName("id 포인트 조회")
    public void detail(){
        long id = 1L;

        UserPoint mockUserPoint = new UserPoint(id, 0, 0); // 예제 데이터
        when(userPointTable.selectById(id)).thenReturn(mockUserPoint);

        UserPoint userPoint = pointService.detail(id);
        //detail에서 selectById 호출시 -> mockUserPoint 이값이 호출되도록 하고있음

        System.out.println("===id serch ===");
        System.out.println(userPoint.id());
        System.out.println(userPoint.point());
        System.out.println("===============");

        assertNotNull(userPoint);
        assertEquals(0L, userPoint.point());
        // selectById()가 1번 호출되었는지 검증
        verify(userPointTable, times(1)).selectById(id);
    }

    @Test
    @DisplayName("포인트 충전 성공")
    public void testPointChargeSuccess(){
        long id = 1L;
        long amountToCharge = 2000L;

        UserPoint existingUserPoint = new UserPoint(id, 100L, 0L); // 기존 포인트 100
        UserPoint updatedUserPoint = new UserPoint(id, 2100L, 0L); // 충전 후 포인트 600
        PointHistory updatePointHistoty = new PointHistory(id, id, amountToCharge, TransactionType.CHARGE, 0L);//저장 포인트 이력
        //mock: selectById 호출 시 기존 데이터 반환
        //detail에서 selectById 호출시 -> existingUserPoint 이값이 호출되는지 확인
        when(userPointTable.selectById(id)).thenReturn(existingUserPoint);
        //mock: insertOrUpdate 호출 시 업데이트된 데이터 반환
        when(userPointTable.insertOrUpdate(id, 2100L)).thenReturn(updatedUserPoint);
        //mock: 포인트 히스토리 저장 호출
        when(pointHistoryTable.insert(id, amountToCharge, TransactionType.CHARGE, 0)).thenReturn(updatePointHistoty);
        // 서비스 호출
        UserPoint result = pointService.charge(id, amountToCharge);

        //결과 검증
        assertNotNull(result);
        assertEquals(2100L, result.point());
        verify(userPointTable, times(1)).insertOrUpdate(id, 2100L); //insertOrUpdate을 1번 호출 되었는지 검증
        verify(pointHistoryTable, times(1)).insert(id, amountToCharge, TransactionType.CHARGE, 0L); // hisroty insert가 1번 호출 되었는지 검증
    }

    @Test
    @DisplayName("포인트 충전시 최대 포인트 초과 예외")
    public void testChargePointExceedsMax(){
        long id = 1L;
        long amountToCharge = 3000L; //충전시 기존 금액 + amountToCharge시 최대 금액 보다 큰 금액으로 셋팅

        UserPoint existingUserPoint = new UserPoint(id, 100L, 0L); // 기존 포인트 100

        //selectById 호출시 기존 데이터 반환
        when(userPointTable.selectById(id)).thenReturn(existingUserPoint);

        //서비스 호출 -> 서비스 호출시 예외발생 여부 확인
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(id, amountToCharge);
        });

        //예외 메시지 검증
        assertEquals("포인트 충전은 3000까지 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("충전금액이 음수일 때 예외")
    public void testChargePointExceedsMin(){
        long id = 1L;
        long amountToCharge = -100L;

        UserPoint existingUserPoint = new UserPoint(id, 100L, 0L);
        when(userPointTable.selectById(id)).thenReturn(existingUserPoint);
        //서비스 호출 -> 서비스 호출시 예외발생 여부 확인
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(id, amountToCharge);
        });
        //예외 메시지 검증
        assertEquals("충전 포인트는 0보다 작을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("포인트 사용 성공")
    public void testPointUseSuccess(){
        long id = 1L;
        long amountToCharge = 500L;

        UserPoint existingUserPoint = new UserPoint(id, 1000L, 0L); //기존 포인트
        UserPoint updatedUserPoint = new UserPoint(id, 500L, 0L); // 사용 후 포인트
        PointHistory updatePointHistoty = new PointHistory(id, id, amountToCharge, TransactionType.USE, 0L);//저장 포인트 이력

        //mock: selectById 호출 시 기존 데이터 반환
        when(userPointTable.selectById(id)).thenReturn(existingUserPoint);
        //mock: insertOrUpdate 호출 시 업데이트된 데이터 반환
        when(userPointTable.insertOrUpdate(id, 500L)).thenReturn(updatedUserPoint);
        //mock: 포인트 히스토리 저장 호출
        when(pointHistoryTable.insert(id, amountToCharge, TransactionType.USE, 0L)).thenReturn(updatePointHistoty);
        // 서비스 호출
        UserPoint result = pointService.use(id, amountToCharge);

        //결과검증
        assertNotNull(result);
        assertEquals(500L, result.point());
        verify(userPointTable, times(1)).insertOrUpdate(id, 500L);
        verify(pointHistoryTable, times(1)).insert(id, amountToCharge, TransactionType.USE, 0L);

    }
    //포인트 사용시 음수일 때 예외
    @Test
    @DisplayName("포인트 사용시 음수일때 예외처리")
    public void testUsePointExceedsMin(){
        long id = 1L;
        long amountToCharge = 200L; //사용할 포인트
        UserPoint existingUserPoint = new UserPoint(id, 100L, 0L);
        when(userPointTable.selectById(id)).thenReturn(existingUserPoint);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pointService.use(id, amountToCharge);
        });
        assertEquals("사용 포인트는 0보다 작을 수 없습니다.", exception.getMessage());
    }

    //포인트 이력 조회
    @Test
    @DisplayName("포인트 저장 이력 없음")
    public void testHistoryThrowsExceptionWhenNoHistory(){
        long id = 1L;

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            pointService.histoty(id);
        });
        assertEquals("포인트 내역이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("id 포인트 수정 이력 조회 성공")
    public void testPointHistorySuccess(){
        // id가 1인 유저의 포인트 사용 내역이 존재
        long id = 1L;
        List<PointHistory> pointHistories = List.of(
                new PointHistory(id, id, 100, TransactionType.CHARGE, 0),
                new PointHistory(id, id, 200, TransactionType.USE, 0),
                new PointHistory(id, id, 100, TransactionType.CHARGE, 0)
        );

        when(pointHistoryTable.selectAllByUserId(id)).thenReturn(pointHistories);

        // When: 포인트 내역 조회
        List<PointHistory> result = pointService.histoty(id);

        assertThat(result).isNotNull().hasSize(3);
    }



}