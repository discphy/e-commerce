package kr.hhplus.be.ecommerce.coupon.infrastructure.jpa;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.ecommerce.coupon.domain.Coupon;
import kr.hhplus.be.ecommerce.coupon.domain.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :couponId")
    Optional<Coupon> findByIdWithLock(Long couponId);

    List<Coupon> findByStatus(CouponStatus status);
}
