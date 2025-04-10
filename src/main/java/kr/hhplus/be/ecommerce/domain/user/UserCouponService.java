package kr.hhplus.be.ecommerce.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public void createUserCoupon(UserCouponCommand.Publish command) {
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

        if (userCoupon.cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        userCoupon.use();
    }
}
