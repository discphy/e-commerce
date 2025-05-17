package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.ecommerce.application.user.UserCouponConstant.MAX_PUBLISH_COUNT_PER_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserCouponServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private UserCouponService userCouponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @DisplayName("유효한 ID로 사용 가능한 쿠폰을 조회해야 한다.")
    @Test
    void getUsableCouponWithInvalidId() {
        // given
        UserCouponCommand.UsableCoupon command = mock(UserCouponCommand.UsableCoupon.class);

        when(userCouponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenThrow(new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> userCouponService.getUsableCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("사용 불가능한 쿠폰은 조회할 수 없다.")
    @Test
    void getUsableCouponCannotUseCoupon() {
        // given
        UserCouponCommand.UsableCoupon command = mock(UserCouponCommand.UsableCoupon.class);

        UserCoupon usedUserCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        when(userCouponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenReturn(usedUserCoupon);

        // when
        assertThatThrownBy(() -> userCouponService.getUsableCoupon(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("사용 가능한 쿠폰을 조회한다.")
    @Test
    void getUsableCoupon() {
        // given
        UserCouponCommand.UsableCoupon command = mock(UserCouponCommand.UsableCoupon.class);

        UserCoupon userCoupon = UserCoupon.builder()
            .id(1L)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        when(userCouponRepository.findByUserIdAndCouponId(anyLong(), anyLong()))
            .thenReturn(userCoupon);

        // when
        UserCouponInfo.UsableCoupon usableCoupon = userCouponService.getUsableCoupon(command);

        // then
        assertThat(usableCoupon.getUserCouponId()).isNotNull();
    }

    @DisplayName("유효한 ID로 쿠폰을 사용할 수 있다.")
    @Test
    void useCouponWithInvalidId() {
        // given
        when(userCouponRepository.findById(anyLong()))
            .thenThrow(new IllegalArgumentException("보유한 쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> userCouponService.useUserCoupon(anyLong()))
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

        when(userCouponRepository.findById(anyLong()))
            .thenReturn(userCoupon);

        // when & then
        assertThatThrownBy(() -> userCouponService.useUserCoupon(anyLong()))
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

        when(userCouponRepository.findById(anyLong()))
            .thenReturn(userCoupon);

        // when
        userCouponService.useUserCoupon(1L);

        // then
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
    }

    @DisplayName("보유 쿠폰 목록을 가져온다.")
    @Test
    void getUserCoupons() {
        // given
        List<UserCoupon> userCoupons = List.of(
            UserCoupon.builder()
                .id(1L)
                .couponId(1L)
                .issuedAt(LocalDateTime.of(2025, 4, 1, 12, 0))
                .build(),
            UserCoupon.builder()
                .id(2L)
                .couponId(2L)
                .issuedAt(LocalDateTime.of(2025, 4, 1, 12, 0))
                .build()
        );

        when(userCouponRepository.findByUserIdAndUsableStatusIn(anyLong(), anyList()))
            .thenReturn(userCoupons);

        // when
        UserCouponInfo.Coupons coupons = userCouponService.getUserCoupons(1L);

        // then
        assertThat(coupons.getCoupons()).hasSize(2)
            .extracting("userCouponId")
            .containsExactlyInAnyOrder(1L, 2L);
    }

    @DisplayName("쿠폰 발급 요청을 실패한다.")
    @Test
    void failedRequestPublishUserCoupon() {
        // given
        UserCouponCommand.PublishRequest command = mock(UserCouponCommand.PublishRequest.class);

        when(userCouponRepository.save(command))
            .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userCouponService.requestPublishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 발급 요청에 실패했습니다.");
    }

    @DisplayName("쿠폰 발급 요청에 성공한다.")
    @Test
    void requestPublishUserCoupon() {
        // given
        UserCouponCommand.PublishRequest command = mock(UserCouponCommand.PublishRequest.class);

        when(userCouponRepository.save(command))
            .thenReturn(true);

        // when
        boolean result = userCouponService.requestPublishUserCoupon(command);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("사용자 쿠폰을 발급한다.")
    @Test
    void publishUserCoupons() {
        // given
        UserCouponCommand.Publish command = UserCouponCommand.Publish.of(1L, 10, MAX_PUBLISH_COUNT_PER_REQUEST);

        when(userCouponRepository.countByCouponId(anyLong()))
            .thenReturn(0);

        List<UserCouponInfo.Candidates> users = List.of(
            UserCouponInfo.Candidates.of(1L, LocalDateTime.of(2025, 4, 1, 12, 0)),
            UserCouponInfo.Candidates.of(2L, LocalDateTime.of(2025, 4, 1, 12, 0))
        );

        when(userCouponRepository.findPublishCandidates(any()))
            .thenReturn(users);

        // when
        userCouponService.publishUserCoupons(command);

        // then
        verify(userCouponRepository, times(1)).saveAll(anyList());
    }

    @DisplayName("발급할 쿠폰 수량이 없는 경우 발급이 진행되지 않는다.")
    @Test
    void publishUserCouponsWithNotRemainCoupon() {
        // given
        UserCouponCommand.Publish command = UserCouponCommand.Publish.of(1L, 10, MAX_PUBLISH_COUNT_PER_REQUEST);

        when(userCouponRepository.countByCouponId(anyLong()))
            .thenReturn(10);

        // when
        userCouponService.publishUserCoupons(command);

        // then
        verify(userCouponRepository, never()).saveAll(anyList());
    }

    @DisplayName("발급 쿠폰 수가 남아 있으면 발급이 진행 중이다.")
    @Test
    void isNotPublishFinished() {
        // given
        UserCouponCommand.PublishFinish command = UserCouponCommand.PublishFinish.of(1L, 10);

        when(userCouponRepository.countByCouponId(anyLong()))
            .thenReturn(9);

        // when
        boolean result = userCouponService.isPublishFinished(command);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("발급 쿠폰 수가 모두 발급되면 발급이 완료된다.")
    @Test
    void isPublishFinished() {
        // given
        UserCouponCommand.PublishFinish command = UserCouponCommand.PublishFinish.of(1L, 10);

        when(userCouponRepository.countByCouponId(anyLong()))
            .thenReturn(10);

        // when
        boolean result = userCouponService.isPublishFinished(command);

        // then
        assertThat(result).isTrue();
    }
}