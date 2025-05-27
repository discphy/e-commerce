package kr.hhplus.be.ecommerce.infrastructure.coupon.repository;

import kr.hhplus.be.ecommerce.domain.coupon.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final UserCouponJpaRepository userCouponJpaRepository;
    private final UserCouponRedisRepository userCouponRedisRepository;
    private final UserCouponQueryDslRepository userCouponQueryDslRepository;
    private final UserCouponJdbcTemplateRepository userCouponJdbcTemplateRepository;

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
    public UserCoupon findByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId)
            .orElseThrow(() -> new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));
    }

    @Override
    public UserCoupon findUserCouponById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId)
            .orElseThrow(() -> new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));
    }

    @Override
    public List<CouponInfo.Coupon> findByUserId(Long userId) {
        return userCouponQueryDslRepository.findByUserId(userId);
    }

    @Override
    public boolean save(CouponCommand.PublishRequest command) {
        return userCouponRedisRepository.save(command);
    }

    @Override
    public int countByCouponId(Long couponId) {
        return userCouponJpaRepository.countByCouponId(couponId);
    }

    @Override
    public List<CouponInfo.Candidates> findPublishCandidates(CouponCommand.Candidates command) {
        return userCouponRedisRepository.findPublishCandidates(command);
    }

    @Override
    public void saveAll(List<UserCoupon> userCoupons) {
        userCouponJdbcTemplateRepository.batchInsert(userCoupons);
    }
}
