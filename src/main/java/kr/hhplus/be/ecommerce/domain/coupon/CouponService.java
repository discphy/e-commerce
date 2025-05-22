package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponClient couponClient;

    @Transactional(readOnly = true)
    public CouponInfo.Coupon getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findCouponById(couponId);
        return CouponInfo.Coupon.builder()
            .couponId(coupon.getId())
            .couponName(coupon.getName())
            .discountRate(coupon.getDiscountRate())
            .build();
    }

    @Transactional(readOnly = true)
    public CouponInfo.UsableCoupon getUsableCoupon(CouponCommand.UsableCoupon command) {
        UserCoupon userCoupon = couponRepository.findByUserIdAndCouponId(command.getUserId(), command.getCouponId());

        if (userCoupon.cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        return CouponInfo.UsableCoupon.of(userCoupon.getId());
    }

    @Transactional
    public void useUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(userCouponId);
        userCoupon.use();
    }

    @Transactional
    public void cancelUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(userCouponId);
        userCoupon.cancel();
    }

    @Transactional(readOnly = true)
    public CouponInfo.Coupons getUserCoupons(Long userId) {
        couponClient.getUser(userId);
        return CouponInfo.Coupons.of(couponRepository.findByUserId(userId));
    }

    public boolean requestPublishUserCoupon(CouponCommand.PublishRequest command) {
        boolean isSuccess = couponRepository.save(command);

        if (!isSuccess) {
            throw new IllegalArgumentException("쿠폰 발급 요청에 실패했습니다.");
        }

        return true;
    }

    @Transactional
    public void publishUserCoupons(CouponCommand.PublishCoupons command) {
        couponRepository.findByStatus(CouponStatus.PUBLISHABLE).stream()
            .map(c -> CouponCommand.PublishCoupon.of(c.getId(), c.getQuantity(), command.getMaxPublishCount()))
            .forEach(this::publishUserCoupons);
    }

    @Transactional
    public void finishedPublishCoupons() {
        couponRepository.findByStatus(CouponStatus.PUBLISHABLE).stream()
            .filter(this::isPublishFinished)
            .forEach(Coupon::finish);
    }

    private boolean isPublishFinished(Coupon coupon) {
        int publishedCount = couponRepository.countByCouponId(coupon.getId());

        if (publishedCount > coupon.getQuantity()) {
            log.error("발급된 쿠폰 개수가 발급 가능 개수를 초과했습니다. 쿠폰 ID: {}", coupon.getId());
        }

        return publishedCount >= coupon.getQuantity();
    }

    private void publishUserCoupons(CouponCommand.PublishCoupon command) {
        int start = couponRepository.countByCouponId(command.getCouponId());
        int end = Math.min(command.getQuantity(), start + command.getMaxPublishCount());

        if (start >= command.getQuantity()) {
            log.info("발급할 쿠폰 수량이 없습니다. 쿠폰 ID : {}", command.getCouponId());
            return;
        }

        List<CouponInfo.Candidates> candidates = couponRepository
            .findPublishCandidates(CouponCommand.Candidates.of(command.getCouponId(), start, end));

        List<UserCoupon> coupons = candidates.stream()
            .map(uc -> UserCoupon.create(uc.getUserId(), command.getCouponId(), uc.getIssuedAt()))
            .toList();

        couponRepository.saveAll(coupons);
    }
}
