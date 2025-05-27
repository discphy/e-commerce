package kr.hhplus.be.ecommerce.domain.coupon;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon findCouponById(Long couponId);

    List<Coupon> findByStatus(CouponStatus status);

    UserCoupon save(UserCoupon userCoupon);

    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    UserCoupon findUserCouponById(Long userCouponId);

    List<CouponInfo.Coupon> findByUserId(Long userId);

    boolean save(CouponCommand.PublishRequest command);

    int countByCouponId(Long couponId);

    List<CouponInfo.Candidates> findPublishCandidates(CouponCommand.Candidates command);

    void saveAll(List<UserCoupon> userCoupons);
}
