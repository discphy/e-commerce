package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class CouponServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("쿠폰이 존재해야 쿠폰 발급이 가능하다.")
    @Test
    void publishCouponShouldCouponExists() {
        // given
        Long couponId = 1L;

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(couponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("발급 상태가 아니면 발급할 수 없다.")
    @Test
    void publishCouponCannotNotPublishableStatus() {
        // given
        Coupon coupon = Coupon.create("쿠폰명",
            0.1,
            10,
            CouponStatus.REGISTERED,
            LocalDateTime.now().plusDays(1)
        );
        couponRepository.save(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(coupon.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰을 발급할 수 없습니다.");
    }

    @DisplayName("만료된 쿠폰은 발급할 수 없다.")
    @Test
    void publishCouponCannotExpired() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .discountRate(0.1)
            .quantity(10)
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().minusDays(1))
            .build();
        couponRepository.save(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(coupon.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰이 만료되었습니다.");
    }

    @DisplayName("쿠폰 수량이 부족하면 발급할 수 없다.")
    @Test
    void publishCouponCannotInsufficientQuantity() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .discountRate(0.1)
            .quantity(0)
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build();
        couponRepository.save(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(coupon.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰 수량이 부족합니다.");
    }

    @DisplayName("쿠폰이 존재해야 쿠폰을 가져올 수 있다.")
    @Test
    void getCouponShouldCouponExists() {
        // given
        Long couponId = 1L;

        // when & then
        assertThatThrownBy(() -> couponService.getCoupon(couponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰을 가져온다.")
    @Test
    void getCoupon() {
        // given
        Coupon coupon = Coupon.create("쿠폰명",
            0.1,
            10,
            CouponStatus.REGISTERED,
            LocalDateTime.now().plusDays(1)
        );
        couponRepository.save(coupon);

        // when
        CouponInfo.Coupon couponInfo = couponService.getCoupon(coupon.getId());

        // then
        assertThat(couponInfo.getCouponId()).isEqualTo(coupon.getId());
        assertThat(couponInfo.getName()).isEqualTo(coupon.getName());
        assertThat(couponInfo.getDiscountRate()).isEqualTo(coupon.getDiscountRate());
    }

    @DisplayName("발급 가능한 쿠폰 목록을 가져온다.")
    @Test
    void getPublishableCoupons() {
        // given
        List<Coupon> coupons = List.of(
            Coupon.builder()
                .name("쿠폰명")
                .status(CouponStatus.PUBLISHABLE)
                .discountRate(0.1)
                .quantity(1)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build(),
            Coupon.builder()
                .name("쿠폰명2")
                .status(CouponStatus.PUBLISHABLE)
                .discountRate(0.1)
                .quantity(10)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build()
        );
        coupons.forEach(couponRepository::save);

        // when
        CouponInfo.PublishableCoupons publishableCoupons = couponService.getPublishableCoupons();

        // then
        assertThat(publishableCoupons.getCoupons()).hasSize(2)
            .extracting(CouponInfo.PublishableCoupon::getCouponId)
            .containsExactlyInAnyOrder(coupons.get(0).getId(), coupons.get(1).getId());
    }

    @DisplayName("쿠폰 발급을 종료한다.")
    @Test
    void finishCoupon() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .quantity(1)
            .build();
        couponRepository.save(coupon);


        // when
        couponService.finishCoupon(coupon.getId());

        // then
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.FINISHED);
    }
}