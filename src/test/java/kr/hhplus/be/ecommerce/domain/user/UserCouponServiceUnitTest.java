package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserCouponServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private UserCouponService userCouponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @DisplayName("사용자 쿠폰을 생성한다.")
    @Test
    void createUserCoupon() {
        // given
        UserCouponCommand.Publish command = mock(UserCouponCommand.Publish.class);

        // when
        userCouponService.createUserCoupon(command);

        // then
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

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
}