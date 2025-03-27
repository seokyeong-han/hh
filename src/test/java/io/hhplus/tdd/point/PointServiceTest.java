package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class PointServiceTest {

    private static final long ANY_ID = 1L;

    private UserPoint createUserPoint(long initialPoint) {
        return new UserPoint(ANY_ID, initialPoint, 0);
    }

    private PointHistory createPointHistory(TransactionType type){
        return new PointHistory(ANY_ID, ANY_ID, 100, type, 0);
    }

    //mock없이 test를 짜보쟈
    @Test
    public void 포인트_조회(){
        UserPoint up = createUserPoint(0);
        UserPoint expectedUserPoint = new UserPoint(ANY_ID, 0, 0); // 기대되는 값

        UserPoint userPoint = up.getUserInfo(up);
        assertThat(userPoint).isEqualTo(expectedUserPoint);
    }

    //포인트 충전 TEST
    //포인트 조회
    //포인트 음수, 최댓값 체크
    //포인트 충전
    //포인트 history insert

    @Test
    public void 포인트_충전(){
        UserPoint userPoint = createUserPoint(0);
        long amount = 1000;
        userPoint = userPoint.addPoint(amount);
        assertThat(userPoint.point()).isEqualTo(1000);
    }

    @Test
    public void 포인트_충전_음수_예외처리(){
        UserPoint userPoint = createUserPoint(0);
        long amount = -100;
        assertThatThrownBy(() -> userPoint.addPoint(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전 포인트는 0보다 작을 수 없습니다.");
    }

    @Test
    public void 최대포인트_3000_넘기면_예외처리(){//포인트 충전 testCase도 만들어야 하는가?
        UserPoint userPoint = new UserPoint(ANY_ID, 2200, 0);
        //assertThatThrownBy() -> 예외발생 예상 검증
        long amount = 1000;
        assertThatThrownBy(() -> userPoint.addPoint(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트 최대 3000까지 가능합니다.");
    }


    ///////////////////////////////////////////////////////////////////////
    /// 포인트 사용 Test Case
    /// 사용포인트는 기존 포인트 보다 많으면 안된다 -> 음수 체크
    /// 포인트 사용
    /// 포인트 사용 history 저장
    @Test
    public void 포인트_음수_예외처리(){ //충전 포인트가 음수일 경우도 다른 testCase로 만들어야 하는가?
        UserPoint userPoint = new UserPoint(ANY_ID, 2000, 0);

        long usePoint = 3000;
        assertThatThrownBy(() -> userPoint.usePoint(usePoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용 포인트는 0보다 작을 수 없습니다.");
    }



    ///////////////////////////////////////////////////////////////////////
    /// 포인트 이력 조회 Test Case
    ///
    @Test
    public void 포인트_이력_조회(){
        List<PointHistory> pointHistories = List.of(
                createPointHistory(TransactionType.CHARGE),
                createPointHistory(TransactionType.USE),
                createPointHistory(TransactionType.CHARGE)
        );

        // when & then
        assertThat(pointHistories).hasSize(3);
        assertThat(pointHistories.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(pointHistories.get(1).type()).isEqualTo(TransactionType.USE);
        assertThat(pointHistories.get(2).type()).isEqualTo(TransactionType.CHARGE);

    }
    @Test
    public void 포인트_수정_이력_없을때(){
        List<PointHistory> pointHistories = List.of(

        );

        assertThat(pointHistories).hasSize(0);
    }







}