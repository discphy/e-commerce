package kr.hhplus.be.ecommerce.domain.user;

import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);
}
