package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.domain.coupon.CouponInfo;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.user.UserCouponInfo;
import kr.hhplus.be.ecommerce.domain.user.UserCouponService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public UserCouponResult.Coupons getUserCoupons(Long userId) {
        userService.getUser(userId);

        List<UserCouponResult.Coupon> coupons = userCouponService.getUserCoupons(userId).getCoupons().stream()
            .map(this::getUserCoupon)
            .toList();
        return UserCouponResult.Coupons.of(coupons);
    }

    private UserCouponResult.Coupon getUserCoupon(UserCouponInfo.Coupon userCoupon) {
        CouponInfo.Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());

        return UserCouponResult.Coupon.builder()
            .userCouponId(userCoupon.getUserCouponId())
            .couponName(coupon.getName())
            .discountRate(coupon.getDiscountRate())
            .build();
    }
}
