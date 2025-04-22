package kr.hhplus.be.ecommerce.infrastructure.coupon;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findWithLockById(Long couponId);
}
