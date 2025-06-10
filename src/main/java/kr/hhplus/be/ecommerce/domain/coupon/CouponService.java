package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponClient couponClient;
    private final CouponRepository couponRepository;
    private final CouponEventPublisher couponEventPublisher;

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
    public CouponInfo.Coupons getUserCoupons(Long userId) {
        couponClient.getUser(userId);
        return CouponInfo.Coupons.of(couponRepository.findByUserId(userId));
    }

    @Transactional(readOnly = true)
    public CouponInfo.Coupon getUsableCoupon(Long userCouponId) {
        UserCoupon userCoupon = couponRepository.findUserCouponById(userCouponId);

        if (userCoupon.cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        return couponRepository.findById(userCouponId);
    }

    public void requestPublishUserCoupon(CouponCommand.Publish command) {
        boolean publishable = couponRepository.findPublishableCouponById(command.getCouponId());

        if (!publishable) {
            throw new IllegalArgumentException("발급 불가한 쿠폰입니다.");
        }

        CouponEvent.PublishRequested event = CouponEvent.PublishRequested.of(command.getUserId(), command.getCouponId());
        couponEventPublisher.publishRequested(event);
    }

    @Transactional
    public void publishUserCoupon(CouponCommand.Publish command) {
        couponRepository.findByUserIdAndCouponId(command.getUserId(), command.getCouponId())
            .ifPresent(coupon -> {
                throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
            });

        Coupon coupon = couponRepository.findCouponById(command.getCouponId());
        coupon.publish();

        UserCoupon userCoupon = UserCoupon.create(command.getUserId(), command.getCouponId());
        couponRepository.save(userCoupon);

        CouponEvent.Published event = CouponEvent.Published.of(coupon);
        couponEventPublisher.published(event);
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
    public void stopPublishCoupon(Long couponId) {
        Coupon coupon = couponRepository.findCouponById(couponId);

        if (coupon.isNotPublishable()) {
            couponRepository.updateAvailableCoupon(couponId, false);
        }
    }
}
