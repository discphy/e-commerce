package kr.hhplus.be.ecommerce.domain.user;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    UserCoupon findById(Long userCouponId);

    List<UserCoupon> findByUserIdAndUsableStatusIn(Long userId, List<UserCouponUsedStatus> statuses);

    Optional<UserCoupon> findOptionalByUserIdAndCouponId(Long userId, Long couponId);
}
