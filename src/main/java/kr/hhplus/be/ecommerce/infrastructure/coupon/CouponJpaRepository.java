package kr.hhplus.be.ecommerce.infrastructure.coupon;

import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
