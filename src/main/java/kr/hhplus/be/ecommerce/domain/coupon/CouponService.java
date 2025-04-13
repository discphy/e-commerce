package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public void publishCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        coupon.publish();
    }

    public CouponInfo.Coupon getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        return CouponInfo.Coupon.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .discountRate(coupon.getDiscountRate())
                .build();
    }
}
