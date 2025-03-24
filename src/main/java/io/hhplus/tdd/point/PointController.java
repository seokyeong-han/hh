package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    @Autowired
    PointService pointService;

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ResponseEntity<UserPoint> point( @PathVariable long id) {
        UserPoint userPoint = pointService.getPoint(id);
        return new ResponseEntity<>(userPoint, HttpStatus.OK);
        //return new UserPoint(0, 0, 0);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return List.of();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(@PathVariable long id, @RequestBody AmountRequest req) {

        UserPoint userPoint = pointService.setPointCharge(id, req.getAmount());
        return null;
        //return new ResponseEntity<>(userPoint, HttpStatus.OK);
        //return new UserPoint(0, 0, 0);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return new UserPoint(0, 0, 0);
    }

    static class AmountRequest {
        public long amount;
//        public AmountRequest() {}  // 기본 생성자 필요 (JSON 파싱용)
//        public AmountRequest(long amount) {
//            this.amount = amount;
//        }
        public long getAmount() {
            return amount;
        }
        @Override
        public String toString() {
            return "AmountRequest{amount=" + amount + "}";
        }
    }
}
