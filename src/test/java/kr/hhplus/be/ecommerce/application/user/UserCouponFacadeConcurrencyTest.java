package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.support.ConcurrencyTestSupport;
import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import kr.hhplus.be.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.be.ecommerce.domain.coupon.CouponStatus;
import kr.hhplus.be.ecommerce.domain.user.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserCouponFacadeConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @DisplayName("동시성 - 한명의 사용자가 동일한 쿠폰을 여러 개 발급할 수 없다.")
    @Test
    void publishUserCouponConcurrencyCannotPublishedMultipleCoupon() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Coupon coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCouponCriteria.Publish criteria = UserCouponCriteria.Publish.of(user.getId(), coupon.getId());

        // when
        executeConcurrency(3, () -> userCouponFacade.publishUserCoupon(criteria));

        // then
        Coupon remainCoupon = couponRepository.findById(coupon.getId());
        assertThat(remainCoupon.getQuantity()).isEqualTo(9);

        UserCoupon publishedCoupon = userCouponRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());
        assertThat(publishedCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
    }

    @DisplayName("동시성 - 쿠폰 발급 수량 보다 많은 쿠폰을 발급할 수 없다.")
    @Test
    void publishUserCouponConcurrencyCannotExceedCouponQuantity() {
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

        // when
        executeConcurrency(List.of(
            () -> userCouponFacade.publishUserCoupon(criteria1),
            () -> userCouponFacade.publishUserCoupon(criteria2),
            () -> userCouponFacade.publishUserCoupon(criteria3)
        ));

        // then
        Coupon remainCoupon = couponRepository.findById(coupon.getId());
        assertThat(remainCoupon.getQuantity()).isZero();

        UserCoupon publishedCoupon1 = userCouponRepository.findByUserIdAndCouponId(user1.getId(), coupon.getId());
        assertThat(publishedCoupon1.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);

        UserCoupon publishedCoupon2 = userCouponRepository.findByUserIdAndCouponId(user2.getId(), coupon.getId());
        assertThat(publishedCoupon2.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);

        assertThatThrownBy(() -> userCouponRepository.findByUserIdAndCouponId(user3.getId(), coupon.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("보유한 쿠폰을 찾을 수 없습니다.");
    }

}