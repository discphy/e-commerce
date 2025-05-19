package kr.hhplus.be.ecommerce.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @DisplayName("쿠폰 이름은 필수다.")
    @Test
    void ofWithoutName() {
        // when & then
        assertThatThrownBy(() -> Coupon.create(null, 0.1, 1, CouponStatus.REGISTERED, LocalDateTime.now().plusDays(1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 이름은 필수입니다.");
    }

    @DisplayName("쿠폰 할인율은 0.0 ~ 1.0 사이여야 한다.")
    @Test
    void ofWithInvalidDiscountRage() {
        // when & then
        assertThatThrownBy(() -> Coupon.create("쿠폰명", 1.1, 1, CouponStatus.REGISTERED, LocalDateTime.now().plusDays(1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 할인율이 올바르지 않습니다.");
    }

    @DisplayName("쿠폰 수량은 0 이상이어야 한다.")
    @Test
    void ofWithInvalidQuantity() {
        // when & then
        assertThatThrownBy(() -> Coupon.create("쿠폰명", 0.1, -1, CouponStatus.REGISTERED, LocalDateTime.now().plusDays(1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 수량은 0 이상이어야 합니다.");
    }

    @DisplayName("쿠폰 상태는 필수다.")
    @Test
    void ofWithoutStatus() {
        // when & then
        assertThatThrownBy(() -> Coupon.create("쿠폰명", 0.1, 1, null, LocalDateTime.now().plusDays(1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 상태는 필수입니다.");
    }

    @DisplayName("쿠폰 만료일은 현재 시간 이후여야 한다.")
    @Test
    void ofWithInvalidExpiredAt() {
        // when & then
        assertThatThrownBy(() -> Coupon.create("쿠폰명", 0.1, 1, CouponStatus.REGISTERED, LocalDateTime.now().minusDays(1)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 만료일은 현재 시간 이후여야 합니다.");
    }

    @DisplayName("쿠폰은 발급 가능 상태일 때, 발급할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"REGISTERED", "CANCELED"})
    void publishWithNonPublishable(CouponStatus status) {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(status)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build();

        // when & then
        assertThatThrownBy(coupon::publish)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰을 발급할 수 없습니다.");
    }

    @DisplayName("쿠폰 만료 기간이 지나지 않았을 때, 발급할 수 있다.")
    @Test
    void publishWithExpired() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().minusDays(1))
            .build();

        // when & then
        assertThatThrownBy(coupon::publish)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰이 만료되었습니다.");
    }

    @DisplayName("쿠폰 수량이 충분할 시, 발급할 수 있다.")
    @Test
    void publishWithInsufficientQuantity() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .quantity(0)
            .build();

        // when & then
        assertThatThrownBy(coupon::publish)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰 수량이 부족합니다.");
    }

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void publish() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .quantity(1)
            .build();

        // when
        coupon.publish();

        // then
        assertThat(coupon.getQuantity()).isZero();
    }

    @DisplayName("쿠폰 발급을 종료한다.")
    @Test
    void finish() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .quantity(1)
            .build();

        // when
        coupon.finish();

        // then
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.FINISHED);
    }
  
}