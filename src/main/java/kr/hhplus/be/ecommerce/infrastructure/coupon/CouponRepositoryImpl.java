package kr.hhplus.be.ecommerce.infrastructure.coupon;

import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import kr.hhplus.be.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.be.ecommerce.domain.coupon.CouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Coupon findById(Long couponId) {
        return couponJpaRepository.findById(couponId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
    }

    @Override
    public Coupon findByIdWithLock(Long couponId) {
        return couponJpaRepository.findByIdWithLock(couponId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
    }

    @Override
    public List<Coupon> findByStatus(CouponStatus status) {
        return couponJpaRepository.findByStatus(status);
    }
}
