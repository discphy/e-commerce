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
}
