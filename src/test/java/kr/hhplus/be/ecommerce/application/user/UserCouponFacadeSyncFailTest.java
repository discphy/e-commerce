package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import kr.hhplus.be.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.be.ecommerce.domain.coupon.CouponStatus;
import kr.hhplus.be.ecommerce.domain.user.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class UserCouponFacadeSyncFailTest extends IntegrationTestSupport {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @DisplayName("동일한 사용자가 동일한 쿠폰을 여러개 발급하는 동시성 문제 발생")
    @Test
    void syncIssueWhenPublishingDuplicateCouponsWithSameUser() throws InterruptedException {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Coupon coupon = Coupon.create("쿠폰명", 0.1, 2, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCouponCriteria.Publish criteria = UserCouponCriteria.Publish.of(user.getId(), coupon.getId());

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userCouponFacade.publishUserCoupon(criteria);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        // 쿠폰 발급이 3회 발생했지만, 쿠폰 수량이 1개만 차감되어있다.
        Coupon remainCoupon = couponRepository.findById(coupon.getId());
        assertThat(remainCoupon.getQuantity()).isEqualTo(1);

        // 정상이라면 쿠폰이 중복으로 발급이 안되어 하나만 발급이 되어야 하는데 3개가 발급이 되어있다.
        List<UserCoupon> publishedCoupon = userCouponRepository.findByUserIdAndUsableStatusIn(user.getId(), List.of(UserCouponUsedStatus.UNUSED));
        assertThat(publishedCoupon).hasSizeGreaterThan(1);
    }

    @DisplayName("여러 명의 사용자가 잔여 수량을 초과해서 쿠폰을 발급받는 동시성 문제 발생")
    @Test
    void syncIssueWhenPublishingDuplicateCouponsWithAnotherUsers() throws InterruptedException {
        // given
        User user1 = User.create("항플");
        userRepository.save(user1);
        User user2 = User.create("항플2");
        userRepository.save(user2);
        User user3 = User.create("항플3");
        userRepository.save(user3);

        Coupon coupon = Coupon.create("쿠폰명", 0.1, 2, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCouponCriteria.Publish criteria1 = UserCouponCriteria.Publish.of(user1.getId(), coupon.getId());
        UserCouponCriteria.Publish criteria2 = UserCouponCriteria.Publish.of(user2.getId(), coupon.getId());
        UserCouponCriteria.Publish criteria3 = UserCouponCriteria.Publish.of(user3.getId(), coupon.getId());
        List<UserCouponCriteria.Publish> criteriaList = List.of(criteria1, criteria2, criteria3);

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executorService.submit(() -> {
                try {
                    userCouponFacade.publishUserCoupon(criteriaList.get(index));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        // 쿠폰 발급이 3회 발생했지만, 쿠폰 수량이 1개만 차감되어있다.
        Coupon remainCoupon = couponRepository.findById(coupon.getId());
        assertThat(remainCoupon.getQuantity()).isEqualTo(1);

        // 정상이라면 쿠폰이 2개만 발급이 되어야 하는데 3개가 발급이 되어있다.
        List<UserCoupon> userCoupon1 = userCouponRepository.findByUserIdAndUsableStatusIn(user1.getId(), List.of(UserCouponUsedStatus.UNUSED));
        assertThat(userCoupon1).hasSize(1);
        List<UserCoupon> userCoupon2 = userCouponRepository.findByUserIdAndUsableStatusIn(user2.getId(), List.of(UserCouponUsedStatus.UNUSED));
        assertThat(userCoupon2).hasSize(1);
        List<UserCoupon> userCoupon3 = userCouponRepository.findByUserIdAndUsableStatusIn(user3.getId(), List.of(UserCouponUsedStatus.UNUSED));
        assertThat(userCoupon3).hasSize(1);
    }

}