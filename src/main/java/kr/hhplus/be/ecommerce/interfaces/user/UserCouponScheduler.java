package kr.hhplus.be.ecommerce.interfaces.user;

import kr.hhplus.be.ecommerce.application.user.UserCouponCriteria;
import kr.hhplus.be.ecommerce.application.user.UserCouponFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.ecommerce.application.user.UserCouponConstant.MAX_PUBLISH_COUNT_PER_REQUEST;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponScheduler {

    private final UserCouponFacade userCouponFacade;

    @Scheduled(cron = "0 * * * * *")
    public void publishUserCoupon() {
        log.info("사용자 쿠폰 발급 등록 스케줄러 실행");
        try {
            userCouponFacade.publishUserCoupons(UserCouponCriteria.Publish.of(MAX_PUBLISH_COUNT_PER_REQUEST));
            log.info("사용자 쿠폰 발급 등록 스케줄러 완료");
        } catch (Exception e) {
            log.error("사용자 쿠폰 발급 등록 스케줄러 실행 중 오류 발생", e);
        }
    }

    @Scheduled(cron = "30 */5 * * * *")
    public void finishedPublishCoupons() {
        log.info("쿠폰 발급 마감 스케줄러 실행");
        try {
            userCouponFacade.finishedPublishCoupons();
            log.info("쿠폰 발급 마감 스케줄러 완료");
        } catch (Exception e) {
            log.error("쿠폰 발급 마감 스케줄러 실행 중 오류 발생", e);
        }
    }
}
