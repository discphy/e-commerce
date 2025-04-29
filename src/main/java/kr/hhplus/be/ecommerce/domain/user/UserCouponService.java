package kr.hhplus.be.ecommerce.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public void createUserCoupon(UserCouponCommand.Publish command) {
        userCouponRepository.findOptionalByUserIdAndCouponId(command.getUserId(), command.getCouponId())
            .ifPresent(
                userCoupon -> {
                    throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
                }
            );

        UserCoupon userCoupon = UserCoupon.create(command.getUserId(), command.getCouponId());
        userCouponRepository.save(userCoupon);
    }

    public UserCouponInfo.UsableCoupon getUsableCoupon(UserCouponCommand.UsableCoupon command) {
        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(command.getUserId(), command.getCouponId());

        if (userCoupon.cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        return UserCouponInfo.UsableCoupon.of(userCoupon.getId());
    }

    public void useUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId);
        userCoupon.use();
    }

    public UserCouponInfo.Coupons getUserCoupons(Long userId) {
        List<UserCoupon> coupons = userCouponRepository.findByUserIdAndUsableStatusIn(userId, UserCouponUsedStatus.forUsable());

        return UserCouponInfo.Coupons.of(coupons.stream()
                .map(UserCouponService::toCouponInfo)
                .toList());
    }

    private static UserCouponInfo.Coupon toCouponInfo(UserCoupon userCoupon) {
        return UserCouponInfo.Coupon.builder()
            .userCouponId(userCoupon.getId())
            .couponId(userCoupon.getCouponId())
            .issuedAt(userCoupon.getIssuedAt())
            .build();
    }
}
