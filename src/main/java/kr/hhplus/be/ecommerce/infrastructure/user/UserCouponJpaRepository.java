package kr.hhplus.be.ecommerce.infrastructure.user;

import kr.hhplus.be.ecommerce.domain.user.UserCoupon;
import kr.hhplus.be.ecommerce.domain.user.UserCouponUsedStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findByUserIdAndUsedStatusIn(Long userId, List<UserCouponUsedStatus> usedStatuses);

    int countByCouponId(Long couponId);

    List<UserCoupon> findByCouponId(Long couponId);
}
