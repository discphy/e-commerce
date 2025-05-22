package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.ecommerce.domain.coupon.CouponConstant.MAX_PUBLISH_COUNT_PER_REQUEST;
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
            .thenReturn(usedUserCoupon);

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
            .thenReturn(userCoupon);

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
        CouponCommand.PublishRequest command = mock(CouponCommand.PublishRequest.class);

        when(couponRepository.save(command))
            .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> couponService.requestPublishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 발급 요청에 실패했습니다.");
    }

    @DisplayName("쿠폰 발급 요청에 성공한다.")
    @Test
    void requestPublishUserCoupon() {
        // given
        CouponCommand.PublishRequest command = mock(CouponCommand.PublishRequest.class);

        when(couponRepository.save(command))
            .thenReturn(true);

        // when
        boolean result = couponService.requestPublishUserCoupon(command);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("발급 가능한 쿠폰이 없으면 발급하지 않는다.")
    @Test
    void publishUserCouponsWithEmptyCoupon() {
        // given
        when(couponRepository.findByStatus(any()))
            .thenReturn(List.of());

        // when
        couponService.publishUserCoupons(CouponCommand.PublishCoupons.of(MAX_PUBLISH_COUNT_PER_REQUEST));

        // then
        verify(couponRepository, never()).saveAll(any());
    }

    @DisplayName("사용자 쿠폰을 발급한다.")
    @Test
    void publishUserCoupons() {
        // given
        CouponCommand.PublishCoupons command = CouponCommand.PublishCoupons.of(MAX_PUBLISH_COUNT_PER_REQUEST);

        when(couponRepository.findByStatus(any()))
            .thenReturn(List.of(
                Coupon.builder()
                    .id(1L)
                    .quantity(10)
                    .build()
            ));


        when(couponRepository.countByCouponId(anyLong()))
            .thenReturn(0);

        List<CouponInfo.Candidates> users = List.of(
            CouponInfo.Candidates.of(1L, LocalDateTime.of(2025, 4, 1, 12, 0)),
            CouponInfo.Candidates.of(2L, LocalDateTime.of(2025, 4, 1, 12, 0))
        );

        when(couponRepository.findPublishCandidates(any()))
            .thenReturn(users);

        // when
        couponService.publishUserCoupons(command);

        // then
        verify(couponRepository, times(1)).saveAll(anyList());
    }

    @DisplayName("발급할 쿠폰 수량이 없는 경우 발급이 진행되지 않는다.")
    @Test
    void publishUserCouponsWithNotRemainCoupon() {
        // given
        CouponCommand.PublishCoupons command = CouponCommand.PublishCoupons.of(MAX_PUBLISH_COUNT_PER_REQUEST);

        when(couponRepository.findByStatus(any()))
            .thenReturn(List.of(
                Coupon.builder()
                    .id(1L)
                    .quantity(10)
                    .build()
            ));

        when(couponRepository.countByCouponId(anyLong()))
            .thenReturn(10);

        // when
        couponService.publishUserCoupons(command);

        // then
        verify(couponRepository, never()).saveAll(any());
    }

    @DisplayName("발급 쿠폰 수가 남아 있으면 발급이 진행 중이다.")
    @Test
    void notFinishedPublishCoupons() {
        // given
        Coupon coupon = mock(Coupon.class);

        when(coupon.getQuantity())
            .thenReturn(10);

        when(couponRepository.findByStatus(any()))
            .thenReturn(List.of(coupon));

        when(couponRepository.countByCouponId(anyLong()))
            .thenReturn(5);

        // when
        couponService.finishedPublishCoupons();

        // then
        verify(coupon, never()).finish();
    }

    @DisplayName("발급 쿠폰 수가 모두 발급되면 발급이 완료된다.")
    @Test
    void finishedPublishCoupons() {
        // given
        Coupon coupon = mock(Coupon.class);

        when(couponRepository.findByStatus(any()))
            .thenReturn(List.of(
                coupon
            ));

        when(couponRepository.countByCouponId(anyLong()))
            .thenReturn(10);

        // when
        couponService.finishedPublishCoupons();

        // then
        verify(coupon, times(1)).finish();
    }
}