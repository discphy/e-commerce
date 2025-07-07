package kr.hhplus.be.ecommerce.coupon.domain;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon findCouponById(Long couponId);

    List<Coupon> findByStatus(CouponStatus status);

    UserCoupon save(UserCoupon userCoupon);

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    UserCoupon findUserCouponById(Long userCouponId);

    CouponInfo.Coupon findById(Long userCouponId);

    List<CouponInfo.Coupon> findByUserId(Long userId);

    boolean findPublishableCouponById(Long couponId);

    void updateAvailableCoupon(Long couponId, boolean status);
}
