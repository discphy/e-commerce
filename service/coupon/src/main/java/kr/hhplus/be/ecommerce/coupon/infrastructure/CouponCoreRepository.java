package kr.hhplus.be.ecommerce.coupon.infrastructure;

import kr.hhplus.be.ecommerce.coupon.domain.*;
import kr.hhplus.be.ecommerce.coupon.infrastructure.jpa.CouponJpaRepository;
import kr.hhplus.be.ecommerce.coupon.infrastructure.jpa.UserCouponJpaRepository;
import kr.hhplus.be.ecommerce.coupon.infrastructure.querydsl.UserCouponQueryDslRepository;
import kr.hhplus.be.ecommerce.coupon.infrastructure.redis.UserCouponRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponCoreRepository implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final UserCouponJpaRepository userCouponJpaRepository;
    private final UserCouponRedisRepository userCouponRedisRepository;
    private final UserCouponQueryDslRepository userCouponQueryDslRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Coupon findCouponById(Long couponId) {
        return couponJpaRepository.findById(couponId)
            .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
    }

    @Override
    public List<Coupon> findByStatus(CouponStatus status) {
        return couponJpaRepository.findByStatus(status);
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public UserCoupon findUserCouponById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId)
            .orElseThrow(() -> new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));
    }

    @Override
    public CouponInfo.Coupon findById(Long userCouponId) {
        return userCouponQueryDslRepository.findById(userCouponId)
            .orElseThrow(() -> new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));
    }

    @Override
    public List<CouponInfo.Coupon> findByUserId(Long userId) {
        return userCouponQueryDslRepository.findByUserId(userId);
    }

    @Override
    public boolean findPublishableCouponById(Long couponId) {
        return userCouponRedisRepository.findPublishableCouponById(couponId);
    }

    @Override
    public void updateAvailableCoupon(Long couponId, boolean available) {
        userCouponRedisRepository.updateAvailable(couponId, available);
    }
}
