package kr.hhplus.be.ecommerce.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserCouponKeyTest {

    @DisplayName("사용자 쿠폰 키 생성")
    @Test
    void of() {
        // given
        UserCouponKey userCouponKey = UserCouponKey.of(1L);

        // when
        String key = userCouponKey.generate();

        // then
        assertThat(key).isEqualTo("user_coupon:1");
    }
}