package kr.hhplus.be.ecommerce.domain.coupon;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Coupon findById(Long couponId);

    Coupon findByIdWithLock(Long couponId);

    List<Coupon> findByStatus(CouponStatus status);
}
