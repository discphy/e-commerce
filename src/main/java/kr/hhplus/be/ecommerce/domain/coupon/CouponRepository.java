package kr.hhplus.be.ecommerce.domain.coupon;

import org.springframework.stereotype.Component;

@Component
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon findById(Long couponId);
}
