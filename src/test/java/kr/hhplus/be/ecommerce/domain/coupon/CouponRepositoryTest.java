package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class CouponRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("쿠폰이 반드시 존재해야 한다.")
    @Test
    void findByIdShouldExists() {
        // given
        Long couponId = 1L;

        // when & then
        assertThatThrownBy(() -> couponRepository.findById(couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰을 가져온다.")
    @Test
    void findById() {
        // given
        Coupon coupon = Coupon.create("쿠폰명",
            0.1,
            10,
            CouponStatus.REGISTERED,
            LocalDateTime.now().plusDays(1)
        );
        couponRepository.save(coupon);

        // when
        Coupon result = couponRepository.findById(coupon.getId());

        // then
        assertThat(result).isEqualTo(coupon);
        assertThat(result.getId()).isNotNull();
    }

}