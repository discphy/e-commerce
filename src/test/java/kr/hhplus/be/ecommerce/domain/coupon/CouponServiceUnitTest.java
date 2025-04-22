package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class CouponServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @DisplayName("유효한 ID로 쿠폰을 발급해야 한다.")
    @Test
    void publishCouponWithInvalidId() {
        // given
        when(couponRepository.findWithLockById(anyLong()))
            .thenThrow(new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(anyLong()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰 발급 가능할 때, 쿠폰을 발급할 수 있다.")
    @Test
    void publishCouponWithCannotPublishable() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.REGISTERED)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build();

        when(couponRepository.findWithLockById(anyLong()))
            .thenReturn(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(anyLong()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰을 발급할 수 없습니다.");
    }

    @DisplayName("쿠폰 만료 기간이 지나지 않았을 때, 쿠폰을 발급할 수 있다.")
    @Test
    void publishCouponWithExpired() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().minusDays(1))
            .build();

        when(couponRepository.findWithLockById(anyLong()))
            .thenReturn(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(anyLong()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰이 만료되었습니다.");
    }

    @DisplayName("쿠폰 수량이 충분할 시, 쿠폰을 발급할 수 있다.")
    @Test
    void publishCouponWithInsufficientQuantity() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .quantity(0)
            .build();

        when(couponRepository.findWithLockById(anyLong()))
            .thenReturn(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(anyLong()))
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

        when(couponRepository.findWithLockById(anyLong()))
            .thenReturn(coupon);

        // when
        couponService.publishCoupon(anyLong());

        // then
        assertThat(coupon.getQuantity()).isZero();
    }

    @DisplayName("유효한 ID로 쿠폰을 조회해야 한다.")
    @Test
    void getCouponWithInvalidId() {
        // given
        when(couponRepository.findById(anyLong()))
            .thenThrow(new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.getCoupon(anyLong()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰을 조회한다.")
    @Test
    void getCoupon() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .discountRate(0.1)
            .quantity(1)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build();

        when(couponRepository.findById(anyLong()))
            .thenReturn(coupon);

        // when
        CouponInfo.Coupon couponInfo = couponService.getCoupon(anyLong());

        // then
        assertThat(couponInfo.getName()).isEqualTo("쿠폰명");
        assertThat(couponInfo.getDiscountRate()).isEqualTo(0.1);
    }

}