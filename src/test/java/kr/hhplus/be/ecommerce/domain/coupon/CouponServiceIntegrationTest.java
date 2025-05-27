package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static kr.hhplus.be.ecommerce.domain.coupon.CouponConstant.MAX_PUBLISH_COUNT_PER_REQUEST;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
class CouponServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @MockitoBean
    private CouponClient couponClient;

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

    @DisplayName("중복된 요청을 보냈을 시, 쿠폰 발급 요청에 실패한다.")
    @Test
    void failedRequestPublishUserCoupon() {
        // given
        CouponCommand.PublishRequest command = CouponCommand.PublishRequest.of(1L, 1L, LocalDateTime.now());
        couponRepository.save(command);

        // when & then
        assertThatThrownBy(() -> couponService.requestPublishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 발급 요청에 실패했습니다.");
    }

    @DisplayName("쿠폰 발급 요청을 한다.")
    @Test
    void requestPublishUserCoupon() {
        // given
        CouponCommand.PublishRequest command = CouponCommand.PublishRequest.of(1L, 1L, LocalDateTime.now());

        // when
        boolean result = couponService.requestPublishUserCoupon(command);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("사용자 쿠폰을 발급한다.")
    @Test
    void publishUserCoupons() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 5, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCoupon userCoupon1 = UserCoupon.create(1L, coupon.getId(), LocalDateTime.now().minusDays(1));
        UserCoupon userCoupon2 = UserCoupon.create(2L, coupon.getId(), LocalDateTime.now().minusDays(1));

        couponRepository.saveAll(List.of(userCoupon1, userCoupon2));

        LongStream.rangeClosed(1, 10)
            .mapToObj(l -> CouponCommand.PublishRequest.of(l, coupon.getId(), LocalDateTime.now().plusSeconds(l)))
            .forEach(couponService::requestPublishUserCoupon);

        CouponCommand.PublishCoupons command = CouponCommand.PublishCoupons.of(MAX_PUBLISH_COUNT_PER_REQUEST);

        // when
        couponService.publishUserCoupons(command);

        // then
        int count = couponRepository.countByCouponId(coupon.getId());
        assertThat(count).isEqualTo(5);
    }

    @DisplayName("사용자 쿠폰 발급 시, 발급 수량이 최대 발급 개수를 초과하면 최대 발급 개수만큼 발급한다.")
    @Test
    void publishUserCouponExceedMaxPublishCountPerRequest() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 1000, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        LongStream.rangeClosed(1, 1000)
            .mapToObj(l -> CouponCommand.PublishRequest.of(l, coupon.getId(), LocalDateTime.now().plusSeconds(l)))
            .forEach(couponService::requestPublishUserCoupon);

        CouponCommand.PublishCoupons command = CouponCommand.PublishCoupons.of(MAX_PUBLISH_COUNT_PER_REQUEST);

        // when
        couponService.publishUserCoupons(command);

        // then
        assertThat(couponRepository.countByCouponId(coupon.getId())).isEqualTo(MAX_PUBLISH_COUNT_PER_REQUEST);
    }

    @DisplayName("발급 쿠폰 수가 남아 있으면 발급이 진행 중이다.")
    @Test
    void notFinishedPublishCoupons() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCoupon userCoupon1 = UserCoupon.create(1L, coupon.getId());
        UserCoupon userCoupon2 = UserCoupon.create(2L, coupon.getId());

        couponRepository.saveAll(List.of(userCoupon1, userCoupon2));

        // when
        couponService.finishedPublishCoupons();

        // then
        Coupon result = couponRepository.findCouponById(coupon.getId());
        assertThat(result.getStatus()).isEqualTo(CouponStatus.PUBLISHABLE);
    }

    @DisplayName("발급 쿠폰 수가 모두 발급되면 발급이 완료된다.")
    @Test
    void finishedPublishCoupons() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 2, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCoupon userCoupon1 = UserCoupon.create(1L, coupon.getId());
        UserCoupon userCoupon2 = UserCoupon.create(2L, coupon.getId());

        couponRepository.saveAll(List.of(userCoupon1, userCoupon2));

        // when
        couponService.finishedPublishCoupons();

        // then
        Coupon result = couponRepository.findCouponById(coupon.getId());
        assertThat(result.getStatus()).isEqualTo(CouponStatus.FINISHED);
    }
}