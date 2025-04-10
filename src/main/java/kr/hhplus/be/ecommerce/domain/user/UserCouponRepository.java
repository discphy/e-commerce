package kr.hhplus.be.ecommerce.domain.user;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    UserCoupon findById(Long userCouponId);

    List<UserCoupon> findByUserIdAndUsableStatusIn(Long userId, List<UserCouponUsedStatus> statuses);
}
