package kr.hhplus.be.ecommerce.coupon.infrastructure.jpa;

import kr.hhplus.be.ecommerce.coupon.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    int countByCouponId(Long couponId);
}
