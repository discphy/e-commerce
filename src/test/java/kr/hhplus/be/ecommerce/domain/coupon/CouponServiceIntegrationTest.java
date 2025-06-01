package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.support.message.Message;
import kr.hhplus.be.ecommerce.support.message.MessageProducer;
import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Transactional
class CouponServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @MockitoBean
    private CouponClient couponClient;

    @MockitoSpyBean
    private CouponEventPublisher couponEventPublisher;

    @MockitoSpyBean
    private MessageProducer messageProducer;

    private CouponInfo.User user;

    @BeforeEach
    void setUp() {
        user = CouponInfo.User.of(1L, "항플");
        when(couponClient.getUser(anyLong()))
            .thenReturn(user);
    }

    @DisplayName("쿠폰이 존재해야 쿠폰을 가져올 수 있다.")
    @Test
    void getCouponShouldCouponExists() {
        // given
        Long couponId = 1L;

        // when & then
        assertThatThrownBy(() -> couponService.getCoupon(couponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰을 가져온다.")
    @Test
    void getCoupon() {
        // given
        Coupon coupon = Coupon.create("쿠폰명",
            0.1,
            10,
            CouponStatus.REGISTERED,
            LocalDateTime.now().plusDays(1)
        );
        couponRepository.save(coupon);

        // when
        CouponInfo.Coupon couponInfo = couponService.getCoupon(coupon.getId());

        // then
        assertThat(couponInfo.getCouponId()).isEqualTo(coupon.getId());
        assertThat(couponInfo.getCouponName()).isEqualTo(coupon.getName());
        assertThat(couponInfo.getDiscountRate()).isEqualTo(coupon.getDiscountRate());
    }

    @DisplayName("사용자 쿠폰이 없으면, 사용 가능한 쿠폰을 가져올 수 없다.")
    @Test
    void getUsableCouponWhenNotFound() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        CouponCommand.UsableCoupon command = CouponCommand.UsableCoupon.of(userId, couponId);

        // when & then
        assertThatThrownBy(() -> couponService.getUsableCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("사용 가능한 상태가 아닌 쿠폰은 가져올 수 없다.")
    @Test
    void getUsableCouponWhenCannotUse() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.builder()
            .userId(userId)
            .couponId(couponId)
            .usedStatus(UserCouponUsedStatus.USED)
            .build();
        couponRepository.save(userCoupon);

        CouponCommand.UsableCoupon command = CouponCommand.UsableCoupon.of(userId, couponId);

        // when & then
        assertThatThrownBy(() -> couponService.getUsableCoupon(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("사용 가능한 쿠폰을 가져온다.")
    @Test
    void getUsableCoupon() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        couponRepository.save(userCoupon);

        CouponCommand.UsableCoupon command = CouponCommand.UsableCoupon.of(userId, couponId);

        // when
        CouponInfo.UsableCoupon usableCoupon = couponService.getUsableCoupon(command);

        // then
        assertThat(usableCoupon.getUserCouponId()).isEqualTo(userCoupon.getId());
    }

    @DisplayName("보유하지 않는 쿠폰은 사용할 수 없다.")
    @Test
    void useUserCouponWhenNotFound() {
        // given
        Long userCouponId = 1L;

        // when & then
        assertThatThrownBy(() -> couponService.useUserCoupon(userCouponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("사용할 수 없는 쿠폰은 사용할 수 없다.")
    @Test
    void useUserCouponWhenCannotUse() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.builder()
            .userId(userId)
            .couponId(couponId)
            .usedStatus(UserCouponUsedStatus.USED)
            .build();
        couponRepository.save(userCoupon);

        // when & then
        assertThatThrownBy(() -> couponService.useUserCoupon(userCoupon.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("사용 가능한 쿠폰을 사용한다.")
    @Test
    void useUserCoupon() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        couponRepository.save(userCoupon);

        // when
        couponService.useUserCoupon(userCoupon.getId());

        // then
        UserCoupon updatedUserCoupon = couponRepository.findUserCouponById(userCoupon.getId());
        assertThat(updatedUserCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
        assertThat(updatedUserCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("사용자 쿠폰이 없으면, 쿠폰을 취소할 수 없다.")
    @Test
    void cancelUserCouponWhenNotFound() {
        // given
        Long userCouponId = 1L;

        // when & then
        assertThatThrownBy(() -> couponService.cancelUserCoupon(userCouponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("사용할 수 있는 쿠폰은 취소할 수 없다.")
    @Test
    void cancelUserCouponWhenCannotUse() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.builder()
            .userId(userId)
            .couponId(couponId)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();
        couponRepository.save(userCoupon);

        // when & then
        assertThatThrownBy(() -> couponService.cancelUserCoupon(userCoupon.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 있는 쿠폰을 취소할 수는 없습니다.");
    }

    @DisplayName("사용한 쿠폰을 취소한다.")
    @Test
    void cancelUserCoupon() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.builder()
            .userId(userId)
            .couponId(couponId)
            .usedStatus(UserCouponUsedStatus.USED)
            .build();
        couponRepository.save(userCoupon);

        // when
        couponService.cancelUserCoupon(userCoupon.getId());

        // then
        UserCoupon updatedUserCoupon = couponRepository.findUserCouponById(userCoupon.getId());
        assertThat(updatedUserCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
        assertThat(updatedUserCoupon.getUsedAt()).isNull();
    }

    @DisplayName("사용자의 쿠폰을 가져올 시, 사용자가 존재해야 한다.")
    @Test
    void getUserCouponsWithNotExistUser() {
        // given
        Long userId = 999L;

        when(couponClient.getUser(anyLong()))
            .thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.getUserCoupons(userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("사용자의 쿠폰을 가져온다.")
    @Test
    void getUserCoupons() {
        // given
        Long anotherUserId = 2L;

        Coupon coupon1 = Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        Coupon coupon2 = Coupon.create("쿠폰명2", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));

        List.of(coupon1, coupon2).forEach(couponRepository::save);

        UserCoupon targetUserCoupon1 = UserCoupon.builder()
            .userId(user.getId())
            .couponId(coupon1.getId())
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        UserCoupon targetUserCoupon2 = UserCoupon.builder()
            .userId(user.getId())
            .couponId(coupon2.getId())
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        UserCoupon anotherUserCoupon = UserCoupon.builder()
            .userId(anotherUserId)
            .couponId(coupon1.getId())
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        List.of(targetUserCoupon1, targetUserCoupon2, anotherUserCoupon).forEach(couponRepository::save);

        // when
        CouponInfo.Coupons coupons = couponService.getUserCoupons(user.getId());

        // then
        assertThat(coupons.getCoupons()).hasSize(2)
            .extracting("userCouponId", "couponId")
            .containsExactlyInAnyOrder(
                tuple(targetUserCoupon1.getId(), targetUserCoupon1.getCouponId()),
                tuple(targetUserCoupon2.getId(), targetUserCoupon2.getCouponId())
            );
    }

    @DisplayName("쿠폰 발급 요청을 실패한다.")
    @Test
    void failedRequestPublishUserCoupon() {
        // given
        Long userId = 1L;
        Long couponId = 2L;
        CouponCommand.Publish command = CouponCommand.Publish.of(userId, couponId);

        // when & then
        assertThatThrownBy(() -> couponService.requestPublishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("발급 불가한 쿠폰입니다.");
    }

    @DisplayName("쿠폰을 발급 요청한다.")
    @Test
    void requestPublishUserCoupon() {
        // given
        Long userId = 1L;

        Coupon coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        couponRepository.updateAvailableCoupon(coupon.getId(), true);

        CouponCommand.Publish command = CouponCommand.Publish.of(userId, coupon.getId());

        // when
        couponService.requestPublishUserCoupon(command);

        // then
        verify(couponEventPublisher, times(1)).publishRequested(any(CouponEvent.PublishRequested.class));
        verify(messageProducer, times(1)). send(any(Message.class));
    }

    @DisplayName("발급된 쿠폰이 없으면 발급되지 않는다.")
    @Test
    void publishUserCouponWithAlreadyPublished() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        couponRepository.save(UserCoupon.create(userId, couponId));

        CouponCommand.Publish command = CouponCommand.Publish.of(userId, couponId);

        // when & then
        assertThatThrownBy(() -> couponService.publishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 발급된 쿠폰입니다.");
    }

    @DisplayName("발급할 쿠폰이 없으면 발급하지 않는다.")
    @Test
    void publishUserCouponWithNoAvailableCoupons() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        CouponCommand.Publish command = CouponCommand.Publish.of(userId, couponId);

        // when & then
        assertThatThrownBy(() -> couponService.publishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void publishUserCouponWithEmptyCoupon() {
        // given
        Long userId = 1L;
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        CouponCommand.Publish command = CouponCommand.Publish.of(userId, coupon.getId());

        // when
        couponService.publishUserCoupon(command);

        // then
        UserCoupon userCoupon = couponRepository.findByUserIdAndCouponId(userId, coupon.getId()).orElseThrow();
        assertThat(userCoupon.getUserId()).isEqualTo(userId);
        assertThat(userCoupon.getCouponId()).isEqualTo(coupon.getId());

        verify(couponEventPublisher, times(1)).published(any(CouponEvent.Published.class));
        assertThat(events.stream(CouponEvent.Published.class).count()).isEqualTo(1);
    }

    @DisplayName("쿠폰이 발급 가능하지 않으면 발급 가능 상태를 변경한다.")
    @Test
    void stopPublishCoupon() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 0, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        couponRepository.updateAvailableCoupon(coupon.getId(), true);

        // when
        couponService.stopPublishCoupon(coupon.getId());

        // then
        boolean available = couponRepository.findPublishableCouponById(coupon.getId());
        assertThat(available).isFalse();
    }
}