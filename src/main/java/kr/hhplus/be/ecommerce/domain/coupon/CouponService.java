package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public void publishCoupon(Long couponId) {
        Coupon coupon = couponRepository.findByIdWithLock(couponId);
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

    public CouponInfo.PublishableCoupons getPublishableCoupons() {
        List<CouponInfo.PublishableCoupon> list = couponRepository.findByStatus(CouponStatus.PUBLISHABLE).stream()
            .map(coupon -> CouponInfo.PublishableCoupon.of(coupon.getId(), coupon.getQuantity()))
            .toList();

        return CouponInfo.PublishableCoupons.of(list);
    }

    public void finishCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        coupon.finish();
    }
}
