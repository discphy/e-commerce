package kr.hhplus.be.ecommerce.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponKeyTest {

    @DisplayName("사용자 쿠폰 키 생성")
    @Test
    void of() {
        // given
        CouponKey couponKey = CouponKey.of(1L);

        // when
        String key = couponKey.generate();

        // then
        assertThat(key).isEqualTo("coupon:1");
    }
}