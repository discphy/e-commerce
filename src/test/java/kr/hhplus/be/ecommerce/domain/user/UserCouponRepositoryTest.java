package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class UserCouponRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @DisplayName("사용자 쿠폰을 생성한다.")
    @Test
    void save() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.create(userId, couponId);

        // when
        userCouponRepository.save(userCoupon);

        // then
        assertThat(userCoupon.getId()).isNotNull();
    }

    @DisplayName("보유한 쿠폰이 없으면 사용자 ID와 쿠폰 ID로 가져올 수 없다.")
    @Test
    void findByUserIdAndCouponIdWhenNotFound() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        // when & then
        assertThatThrownBy(() -> userCouponRepository.findByUserIdAndCouponId(userId, couponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("보유한 쿠폰을 사용자 ID와 쿠폰ID로 가져온다.")
    @Test
    void findByUserIdAndCouponId() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        userCouponRepository.save(userCoupon);

        // when
        UserCoupon result = userCouponRepository.findByUserIdAndCouponId(userId, couponId);

        // then
        assertThat(result.getId()).isEqualTo(userCoupon.getId());
        assertThat(result.getUserId()).isEqualTo(userCoupon.getUserId());
        assertThat(result.getCouponId()).isEqualTo(userCoupon.getCouponId());
    }

    @DisplayName("보유한 쿠폰이 없으면 사용자 쿠폰 ID로 가져올 수 없다.")
    @Test
    void findByIdWhenNotFound() {
        // given
        Long userCouponId = 1L;

        // when & then
        assertThatThrownBy(() -> userCouponRepository.findById(userCouponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("보유한 쿠폰을 사용자 쿠폰 ID로 가져온다.")
    @Test
    void findById() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        userCouponRepository.save(userCoupon);

        // when
        UserCoupon result = userCouponRepository.findById(userCoupon.getId());

        // then
        assertThat(result.getId()).isEqualTo(userCoupon.getId());
        assertThat(result.getUserId()).isEqualTo(userCoupon.getUserId());
        assertThat(result.getCouponId()).isEqualTo(userCoupon.getCouponId());
    }

    @DisplayName("보유한 쿠폰을 사용자 ID와 사용 가능 상태값들로 가져온다.")
    @Test
    void findByUserIdAndUsableStatusIn() {
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
        List<UserCoupon> result = userCouponRepository.findByUserIdAndUsableStatusIn(userId, List.of(UserCouponUsedStatus.UNUSED));

        // then
        assertThat(result).hasSize(1)
            .extracting("userId", "couponId", "usedStatus")
            .containsExactlyInAnyOrder(
                tuple(targetUserCoupon.getUserId(), targetUserCoupon.getCouponId(), targetUserCoupon.getUsedStatus())
            );
    }

}