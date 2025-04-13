package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import kr.hhplus.be.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.be.ecommerce.domain.coupon.CouponStatus;
import kr.hhplus.be.ecommerce.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@Transactional
class UserCouponFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    private User user;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);

        coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);
    }

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void publishUserCoupon() {
        // given
        UserCouponCriteria.Publish criteria = UserCouponCriteria.Publish.of(user.getId(), coupon.getId());

        // when
        userCouponFacade.publishUserCoupon(criteria);

        // then
        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());
        assertThat(userCoupon).isNotNull();
        assertThat(userCoupon.getUserId()).isEqualTo(user.getId());
        assertThat(userCoupon.getCouponId()).isEqualTo(coupon.getId());
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
    }

    @DisplayName("보유 쿠폰 목록을 조회한다.")
    @Test
    void getUserCoupons() {
        // given
        Coupon anotherCoupon = Coupon.create("쿠폰명2", 0.2, 30, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(anotherCoupon);

        UserCoupon userCoupon1 = UserCoupon.create(user.getId(), coupon.getId());
        UserCoupon userCoupon2 = UserCoupon.create(user.getId(), anotherCoupon.getId());
        userCouponRepository.save(userCoupon1);
        userCouponRepository.save(userCoupon2);

        // when
        UserCouponResult.Coupons userCoupons = userCouponFacade.getUserCoupons(user.getId());

        // then
        assertThat(userCoupons.getCoupons()).hasSize(2)
            .extracting("userCouponId", "couponName", "discountRate")
            .containsExactlyInAnyOrder(
                tuple(userCoupon1.getId(), coupon.getName(), coupon.getDiscountRate()),
                tuple(userCoupon2.getId(), anotherCoupon.getName(), anotherCoupon.getDiscountRate())
            );
    }

}