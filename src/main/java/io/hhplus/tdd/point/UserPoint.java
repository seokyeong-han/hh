package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {


    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint getUserInfo(UserPoint up) {
        UserPoint userPoint = new UserPoint(up.id, up.point, 0);
        return userPoint;
    }

    public UserPoint addPoint(long amount) {
        long maxPoint = 3000;
        if(amount < 0) {
            throw new IllegalArgumentException("충전 포인트는 0보다 작을 수 없습니다.");
        }

        if(this.point+amount > maxPoint) {
            throw new IllegalArgumentException("포인트 최대 3000까지 가능합니다.");
        }

        return new UserPoint(id, this.point + amount, 0);
    }

    public void usePoint(long usePoint) {
        if(this.point - usePoint < 0) {
            throw new IllegalArgumentException("사용 포인트는 0보다 작을 수 없습니다.");
        }
    }
}