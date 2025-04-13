package kr.hhplus.be.ecommerce.infrastructure.user;

import kr.hhplus.be.ecommerce.domain.user.UserCoupon;
import kr.hhplus.be.ecommerce.domain.user.UserCouponRepository;
import kr.hhplus.be.ecommerce.domain.user.UserCouponUsedStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCouponRepositoryImpl implements UserCouponRepository {

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return null;
    }

    @Override
    public UserCoupon findByUserIdAndCouponId(Long userId, Long couponId) {
        return null;
    }

    @Override
    public UserCoupon findById(Long userCouponId) {
        return null;
    }

    @Override
    public List<UserCoupon> findByUserIdAndUsableStatusIn(Long userId, List<UserCouponUsedStatus> statuses) {
        return List.of();
    }
}
