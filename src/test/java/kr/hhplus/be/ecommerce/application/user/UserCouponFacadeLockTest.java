package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import kr.hhplus.be.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.be.ecommerce.domain.coupon.CouponStatus;
import kr.hhplus.be.ecommerce.domain.user.User;
import kr.hhplus.be.ecommerce.domain.user.UserRepository;
import kr.hhplus.be.ecommerce.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class UserCouponFacadeLockTest extends ConcurrencyTestSupport {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("분산락 - 동시에 선착순 발급 시, 모든 요청에 대해 발급 되어야 한다.")
    @Test
    void publishCouponWithDistributedLock() {
        // given
        User user1 = User.create("항플러1");
        userRepository.save(user1);

        User user2 = User.create("항플러2");
        userRepository.save(user2);

        Coupon coupon = Coupon.create("쿠폰명", 0.1, 5, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCouponCriteria.Publish criteria1 = UserCouponCriteria.Publish.of(user1.getId(), coupon.getId());
        UserCouponCriteria.Publish criteria2 = UserCouponCriteria.Publish.of(user2.getId(), coupon.getId());

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(List.of(
            () -> {
                try {
                    userCouponFacade.publishUserCoupon(criteria1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            },
            () -> {
                try {
                    userCouponFacade.publishUserCoupon(criteria2);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            }
        ));

        // then
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failCount.get()).isZero();

        Coupon remainCoupon = couponRepository.findById(coupon.getId());
        assertThat(remainCoupon.getQuantity()).isEqualTo(3);
    }

}