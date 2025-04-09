package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.user.UserCouponService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCouponFacade {

    private final UserService userService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;

    public void publishUserCoupon(UserCouponCriteria.Publish criteria) {
        userService.getUser(criteria.getUserId());
        couponService.publishCoupon(criteria.getCouponId());
        userCouponService.createUserCoupon(criteria.toCommand());
    }
}
