package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CouponServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponClient couponClient;

    @Mock
    private CouponEventPublisher couponEventPublisher;

    @DisplayName("유효한 ID로 쿠폰을 조회해야 한다.")
    @Test
    void getCouponWithInvalidId() {
        // given
        when(couponRepository.findCouponById(anyLong()))
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

        when(couponRepository.findCouponById(anyLong()))
            .thenReturn(coupon);

        // when
        CouponInfo.Coupon couponInfo = couponService.getCoupon(anyLong());

        // then
        assertThat(couponInfo.getCouponName()).isEqualTo("쿠폰명");
        assertThat(couponInfo.getDiscountRate()).isEqualTo(0.1);
    }

    @DisplayName("유효한 ID로 사용 가능한 쿠폰을 조회해야 한다.")
    @Test
    void getUsableCouponWithInvalidId() {
        // given
        CouponCommand.UsableCoupon command = mock(CouponCommand.UsableCoupon.class);

        when(couponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenThrow(new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.getUsableCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("사용 불가능한 쿠폰은 조회할 수 없다.")
    @Test
    void getUsableCouponCannotUseCoupon() {
        // given
        CouponCommand.UsableCoupon command = mock(CouponCommand.UsableCoupon.class);

        UserCoupon usedUserCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        when(couponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenReturn(Optional.of(usedUserCoupon));

        // when
        assertThatThrownBy(() -> couponService.getUsableCoupon(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("사용 가능한 쿠폰을 조회한다.")
    @Test
    void getUsableCoupon() {
        // given
        CouponCommand.UsableCoupon command = mock(CouponCommand.UsableCoupon.class);

        UserCoupon userCoupon = UserCoupon.builder()
            .id(1L)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        when(couponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenReturn(Optional.of(userCoupon));

        // when
        CouponInfo.UsableCoupon usableCoupon = couponService.getUsableCoupon(command);

        // then
        assertThat(usableCoupon.getUserCouponId()).isNotNull();
    }

    @DisplayName("유효한 ID로 쿠폰을 사용할 수 있다.")
    @Test
    void useCouponWithInvalidId() {
        // given
        when(couponRepository.findUserCouponById(anyLong()))
            .thenThrow(new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.useUserCoupon(anyLong()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("사용 불가능한 쿠폰은 사용할 수 없다.")
    @Test
    void useCouponCannotUseCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        when(couponRepository.findUserCouponById(anyLong()))
            .thenReturn(userCoupon);

        // when & then
        assertThatThrownBy(() -> couponService.useUserCoupon(anyLong()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("쿠폰을 사용한다.")
    @Test
    void useCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .id(1L)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        when(couponRepository.findUserCouponById(anyLong()))
            .thenReturn(userCoupon);

        // when
        couponService.useUserCoupon(1L);

        // then
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
    }

    @DisplayName("유효한 ID로 쿠폰을 취소할 수 있다.")
    @Test
    void cancelCouponWithInvalidId() {
        // given
        when(couponRepository.findUserCouponById(anyLong()))
            .thenThrow(new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.useUserCoupon(anyLong()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("사용 가능한 쿠폰은 취소할 수 없다.")
    @Test
    void cancelCouponCannotUseCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        when(couponRepository.findUserCouponById(anyLong()))
            .thenReturn(userCoupon);

        // when & then
        assertThatThrownBy(() -> couponService.cancelUserCoupon(anyLong()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 있는 쿠폰을 취소할 수는 없습니다.");
    }

    @DisplayName("쿠폰을 취소한다.")
    @Test
    void cancelCoupon() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .id(1L)
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        when(couponRepository.findUserCouponById(anyLong()))
            .thenReturn(userCoupon);

        // when
        couponService.cancelUserCoupon(1L);

        // then
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
        assertThat(userCoupon.getUsedAt()).isNull();
    }

    @DisplayName("보유 쿠폰 목록 가져올 시, 사용자가 존재해야 한다.")
    @Test
    void getUserCouponsWithNotExistUser() {
        // given
        when(couponClient.getUser(anyLong()))
            .thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.getUserCoupons(anyLong()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("보유 쿠폰 목록을 가져온다.")
    @Test
    void getUserCoupons() {
        // given
        List<CouponInfo.Coupon> userCoupons = List.of(
            CouponInfo.Coupon.builder()
                .userCouponId(1L)
                .couponId(1L)
                .couponName("쿠폰명")
                .discountRate(0.2)
                .issuedAt(LocalDateTime.of(2025, 4, 1, 12, 0))
                .build(),
            CouponInfo.Coupon.builder()
                .userCouponId(2L)
                .couponId(2L)
                .couponName("쿠폰명2")
                .discountRate(0.1)
                .issuedAt(LocalDateTime.of(2025, 4, 1, 12, 0))
                .build()
        );

        when(couponRepository.findByUserId(anyLong()))
            .thenReturn(userCoupons);

        // when
        CouponInfo.Coupons coupons = couponService.getUserCoupons(1L);

        // then
        assertThat(coupons.getCoupons()).hasSize(2)
            .extracting("userCouponId")
            .containsExactlyInAnyOrder(1L, 2L);
    }

    @DisplayName("쿠폰 발급 요청을 실패한다.")
    @Test
    void failedRequestPublishUserCoupon() {
        // given
        CouponCommand.Publish command = mock(CouponCommand.Publish.class);

        when(couponRepository.findPublishableCouponById(command.getCouponId()))
            .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> couponService.requestPublishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("발급 불가한 쿠폰입니다.");
    }

    @DisplayName("쿠폰 발급 요청에 성공한다.")
    @Test
    void requestPublishUserCoupon() {
        // given
        CouponCommand.Publish command = mock(CouponCommand.Publish.class);

        when(couponRepository.findPublishableCouponById(anyLong()))
            .thenReturn(true);

        // when
        couponService.requestPublishUserCoupon(command);

        // then
        verify(couponEventPublisher).publishRequested(any(CouponEvent.PublishRequested.class));
    }

    @DisplayName("발급된 쿠폰이 없으면 발급되지 않는다.")
    @Test
    void publishUserCouponWithAlreadyPublished() {
        // given
        CouponCommand.Publish command = mock(CouponCommand.Publish.class);

        when(couponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenReturn(Optional.of(UserCoupon.builder().id(1L).build()));

        // when & then
        assertThatThrownBy(() -> couponService.publishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 발급된 쿠폰입니다.");
    }

    @DisplayName("발급할 쿠폰이 없으면 발급하지 않는다.")
    @Test
    void publishUserCouponWithNoAvailableCoupons() {
        // given
        CouponCommand.Publish command = mock(CouponCommand.Publish.class);

        when(couponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        when(couponRepository.findCouponById(anyLong()))
            .thenThrow(new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.publishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void publishUserCouponWithEmptyCoupon() {
        // given
        CouponCommand.Publish command = mock(CouponCommand.Publish.class);

        when(couponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        when(couponRepository.findCouponById(anyLong()))
            .thenReturn(Coupon.builder()
                .name("쿠폰명")
                .status(CouponStatus.PUBLISHABLE)
                .discountRate(0.1)
                .quantity(1)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build()
            );

        // when
        couponService.publishUserCoupon(command);

        // then
        verify(couponRepository).save(any(UserCoupon.class));
        verify(couponEventPublisher).published(any(CouponEvent.Published.class));
    }

    @DisplayName("쿠폰이 발급 가능하지 않으면 발급 가능 상태를 변경한다.")
    @Test
    void stopPublishCoupon() {
        // given
        Coupon coupon = mock(Coupon.class);

        when(couponRepository.findCouponById(anyLong()))
            .thenReturn(coupon);

        when(coupon.isNotPublishable())
            .thenReturn(true);

        // when
        couponService.stopPublishCoupon(1L);

        // then
        verify(couponRepository).updateAvailableCoupon(anyLong(), eq(false));
    }

    @DisplayName("쿠폰이 발급 가능하면 발급 가능 상태를 변경하지 않는다.")
    @Test
    void stopPublishCouponWithPublishable() {
        // given
        Coupon coupon = mock(Coupon.class);

        when(couponRepository.findCouponById(anyLong()))
            .thenReturn(coupon);

        when(coupon.isNotPublishable())
            .thenReturn(false);

        // when
        couponService.stopPublishCoupon(1L);

        // then
        verify(couponRepository, never()).updateAvailableCoupon(anyLong(), eq(false));
    }
}