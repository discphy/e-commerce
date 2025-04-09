package kr.hhplus.be.ecommerce.infrastructure.coupon;

import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import kr.hhplus.be.ecommerce.domain.coupon.CouponRepository;
import org.springframework.stereotype.Component;

@Component
public class CouponRepositoryImpl implements CouponRepository {

    @Override
    public Coupon findById(Long couponId) {
        return null;
    }
}
