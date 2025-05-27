package kr.hhplus.be.ecommerce.interfaces.coupon.scheduler;

import kr.hhplus.be.ecommerce.domain.coupon.CouponCommand;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.ecommerce.domain.coupon.CouponConstant.MAX_PUBLISH_COUNT_PER_REQUEST;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponService couponService;

    @Scheduled(cron = "0 * * * * *")
    public void publishUserCoupon() {
        log.info("사용자 쿠폰 발급 등록 스케줄러 실행");
        try {
            couponService.publishUserCoupons(CouponCommand.PublishCoupons.of(MAX_PUBLISH_COUNT_PER_REQUEST));
            log.info("사용자 쿠폰 발급 등록 스케줄러 완료");
        } catch (Exception e) {
            log.error("사용자 쿠폰 발급 등록 스케줄러 실행 중 오류 발생", e);
        }
    }

    @Scheduled(cron = "30 */5 * * * *")
    public void finishedPublishCoupons() {
        log.info("쿠폰 발급 마감 스케줄러 실행");
        try {
            couponService.finishedPublishCoupons();
            log.info("쿠폰 발급 마감 스케줄러 완료");
        } catch (Exception e) {
            log.error("쿠폰 발급 마감 스케줄러 실행 중 오류 발생", e);
        }
    }
}
