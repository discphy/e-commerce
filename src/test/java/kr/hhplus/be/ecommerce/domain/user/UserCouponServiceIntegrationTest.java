package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class UserCouponServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @DisplayName("이미 발급받은 쿠폰은 재발급할 수 없다.")
    @Test
    void createUserCouponCannotDuplicate() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        userCouponRepository.save(UserCoupon.create(userId, couponId));

        UserCouponCommand.Publish command = UserCouponCommand.Publish.of(userId, couponId);

        // when & then
        assertThatThrownBy(() -> userCouponService.createUserCoupon(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 발급된 쿠폰입니다.");
    }

    @DisplayName("사용자 쿠폰을 생성한다.")
    @Test
    void createUserCoupon() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        UserCouponCommand.Publish command = UserCouponCommand.Publish.of(userId, couponId);

        // when
        userCouponService.createUserCoupon(command);

        // then
        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(userId, couponId);
        assertThat(userCoupon).isNotNull();
        assertThat(userCoupon.getUserId()).isEqualTo(userId);
        assertThat(userCoupon.getCouponId()).isEqualTo(couponId);
        assertThat(userCoupon.getIssuedAt()).isNotNull();
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
    }

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
}