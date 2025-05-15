package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static kr.hhplus.be.ecommerce.application.user.UserCouponConstant.MAX_PUBLISH_COUNT_PER_REQUEST;
import static org.assertj.core.api.Assertions.*;

@Transactional
class UserCouponServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @DisplayName("사용자 쿠폰이 없으면, 사용 가능한 쿠폰을 가져올 수 없다.")
    @Test
    void getUsableCouponWhenNotFound() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCouponCommand.UsableCoupon command = UserCouponCommand.UsableCoupon.of(userId, couponId);

        // when & then
        assertThatThrownBy(() -> userCouponService.getUsableCoupon(command))
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
        userCouponRepository.save(userCoupon);

        UserCouponCommand.UsableCoupon command = UserCouponCommand.UsableCoupon.of(userId, couponId);

        // when & then
        assertThatThrownBy(() -> userCouponService.getUsableCoupon(command))
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
        userCouponRepository.save(userCoupon);

        UserCouponCommand.UsableCoupon command = UserCouponCommand.UsableCoupon.of(userId, couponId);

        // when
        UserCouponInfo.UsableCoupon usableCoupon = userCouponService.getUsableCoupon(command);

        // then
        assertThat(usableCoupon.getUserCouponId()).isEqualTo(userCoupon.getId());
    }

    @DisplayName("보유하지 않는 쿠폰은 사용할 수 없다.")
    @Test
    void useUserCouponWhenNotFound() {
        // given
        Long userCouponId = 1L;

        // when & then
        assertThatThrownBy(() -> userCouponService.useUserCoupon(userCouponId))
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
        userCouponRepository.save(userCoupon);

        // when & then
        assertThatThrownBy(() -> userCouponService.useUserCoupon(userCoupon.getId()))
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
        userCouponRepository.save(userCoupon);

        // when
        userCouponService.useUserCoupon(userCoupon.getId());

        // then
        UserCoupon updatedUserCoupon = userCouponRepository.findById(userCoupon.getId());
        assertThat(updatedUserCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
        assertThat(updatedUserCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("사용자의 쿠폰을 가져온다.")
    @Test
    void getUserCoupons() {
        // given
        Long userId = 1L;
        Long anotherUserId = 2L;

        UserCoupon userCoupon1 = UserCoupon.builder()
            .userId(userId)
            .couponId(1L)
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        UserCoupon targetUserCoupon = UserCoupon.builder()
            .userId(userId)
            .couponId(2L)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        UserCoupon userCoupon3 = UserCoupon.builder()
            .userId(anotherUserId)
            .couponId(3L)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        List.of(userCoupon1, targetUserCoupon, userCoupon3).forEach(userCouponRepository::save);


        // when
        UserCouponInfo.Coupons coupons = userCouponService.getUserCoupons(userId);

        // then
        assertThat(coupons.getCoupons()).hasSize(1)
            .extracting("userCouponId", "couponId")
            .containsExactlyInAnyOrder(
                tuple(targetUserCoupon.getId(), targetUserCoupon.getCouponId())
            );
    }

    @DisplayName("중복된 요청을 보냈을 시, 쿠폰 발급 요청에 실패한다.")
    @Test
    void failedRequestPublishUserCoupon() {
        // given
        UserCouponCommand.PublishRequest command = UserCouponCommand.PublishRequest.of(1L, 1L, LocalDateTime.now());
        userCouponRepository.save(command);

        // when & then
        assertThatThrownBy(() -> userCouponService.requestPublishUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 발급 요청에 실패했습니다.");
    }

    @DisplayName("쿠폰 발급 요청을 한다.")
    @Test
    void requestPublishUserCoupon() {
        // given
        UserCouponCommand.PublishRequest command = UserCouponCommand.PublishRequest.of(1L, 1L, LocalDateTime.now());

        // when
        boolean result = userCouponService.requestPublishUserCoupon(command);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("사용자 쿠폰을 발급한다.")
    @Test
    void publishUserCoupons() {
        // given
        long couponId = 1L;
        UserCoupon userCoupon1 = UserCoupon.create(1L, couponId, LocalDateTime.now().minusDays(1));
        UserCoupon userCoupon2 = UserCoupon.create(2L, couponId, LocalDateTime.now().minusDays(1));

        userCouponRepository.saveAll(List.of(userCoupon1, userCoupon2));

        LongStream.rangeClosed(1, 10)
            .mapToObj(l -> UserCouponCommand.PublishRequest.of(l, couponId, LocalDateTime.now().plusSeconds(l)))
            .forEach(userCouponService::requestPublishUserCoupon);

        UserCouponCommand.Publish command = UserCouponCommand.Publish.of(couponId, 5, MAX_PUBLISH_COUNT_PER_REQUEST);

        // when
        userCouponService.publishUserCoupons(command);

        // then
        List<UserCoupon> coupons = userCouponRepository.findCouponId(couponId);
        assertThat(coupons).hasSize(5)
            .extracting("userId")
            .containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @DisplayName("사용자 쿠폰 발급 시, 발급 수량이 최대 발급 개수를 초과하면 최대 발급 개수만큼 발급한다.")
    @Test
    void publishUserCouponExceedMaxPublishCountPerRequest() {
        // given
        long couponId = 1L;
        UserCouponCommand.Publish command = UserCouponCommand.Publish.of(couponId, 1000, MAX_PUBLISH_COUNT_PER_REQUEST);

        LongStream.rangeClosed(1, 1000)
            .mapToObj(l -> UserCouponCommand.PublishRequest.of(l, couponId, LocalDateTime.now().plusSeconds(l)))
            .forEach(userCouponService::requestPublishUserCoupon);

        // when
        userCouponService.publishUserCoupons(command);

        // then
        assertThat(userCouponRepository.countByCouponId(couponId)).isEqualTo(MAX_PUBLISH_COUNT_PER_REQUEST);
    }

    @DisplayName("발급 쿠폰 수가 남아 있으면 발급이 진행 중이다.")
    @Test
    void isNotPublishFinished() {
        // given
        long couponId = 1L;
        UserCoupon userCoupon1 = UserCoupon.create(1L, couponId);
        UserCoupon userCoupon2 = UserCoupon.create(2L, couponId);

        userCouponRepository.saveAll(List.of(userCoupon1, userCoupon2));

        UserCouponCommand.PublishFinish command = UserCouponCommand.PublishFinish.of(couponId, 5);

        // when
        boolean result = userCouponService.isPublishFinished(command);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("발급 쿠폰 수가 모두 발급되면 발급이 완료된다.")
    @Test
    void isPublishFinished() {
        // given
        long couponId = 1L;
        UserCoupon userCoupon1 = UserCoupon.create(1L, couponId);
        UserCoupon userCoupon2 = UserCoupon.create(2L, couponId);

        userCouponRepository.saveAll(List.of(userCoupon1, userCoupon2));

        UserCouponCommand.PublishFinish command = UserCouponCommand.PublishFinish.of(couponId, 2);

        // when
        boolean result = userCouponService.isPublishFinished(command);

        // then
        assertThat(result).isTrue();
    }
}