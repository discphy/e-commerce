package kr.hhplus.be.ecommerce.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserCouponTest {

    @DisplayName("사용할 수 없는 쿠폰인지 확인한다.")
    @Test
    void cannotUse() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        // when
        boolean isUse = userCoupon.cannotUse();

        // then
        assertThat(isUse).isTrue();
    }

    @DisplayName("사용할 수 없는 쿠폰을 사용할 수 없다.")
    @Test
    void useWithCannotUseCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        // when & then
        assertThatThrownBy(userCoupon::use)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("사용할 수 있는 쿠폰을 사용한다.")
    @Test
    void use() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        // when
        userCoupon.use();

        // then
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
    }

    @DisplayName("사용할 수 있는 쿠폰을 취소할 수 없다.")
    @Test
    void cancelWithCanUseCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        // when & then
        assertThatThrownBy(userCoupon::cancel)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 있는 쿠폰을 취소할 수는 없습니다.");
    }

    @DisplayName("쿠폰을 취소한다.")
    @Test
    void cancel() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        // when
        userCoupon.cancel();

        // then
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
        assertThat(userCoupon.getUsedAt()).isNull();
    }
}