package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CouponServiceConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("동시에 선착순 발급 시, 모든 요청에 대해 발급 되어야 한다.")
    @Test
    void publishCouponWithPessimisticWriteLock() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 5, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(2, () -> {
            try {
                couponService.publishCoupon(coupon.getId());
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failCount.get()).isZero();

        Coupon remainCoupon = couponRepository.findById(coupon.getId());
        assertThat(remainCoupon.getQuantity()).isEqualTo(3);
    }

    @DisplayName("동시에 선착순 발급 시, 쿠폰이 부족하면 예외가 발생한다.")
    @Test
    void publishCouponWithPessimisticWriteLockWhenInsufficientCoupon() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 1, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(2, () -> {
            try {
                couponService.publishCoupon(coupon.getId());
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Coupon remainCoupon = couponRepository.findById(coupon.getId());
        assertThat(remainCoupon.getQuantity()).isZero();
    }

}