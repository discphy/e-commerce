package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class CouponRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("쿠폰이 반드시 존재해야 한다.")
    @Test
    void findCouponByIdShouldExists() {
        // given
        Long couponId = 1L;

        // when & then
        assertThatThrownBy(() -> couponRepository.findCouponById(couponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰을 가져온다.")
    @Test
    void findCouponById() {
        // given
        Coupon coupon = Coupon.create("쿠폰명",
            0.1,
            10,
            CouponStatus.REGISTERED,
            LocalDateTime.now().plusDays(1)
        );
        couponRepository.save(coupon);

        // when
        Coupon result = couponRepository.findCouponById(coupon.getId());

        // then
        assertThat(result).isEqualTo(coupon);
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("사용자 쿠폰을 생성한다.")
    @Test
    void save() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.create(userId, couponId);

        // when
        couponRepository.save(userCoupon);

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
        assertThatThrownBy(() -> couponRepository.findByUserIdAndCouponId(userId, couponId))
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
        couponRepository.save(userCoupon);

        // when
        UserCoupon result = couponRepository.findByUserIdAndCouponId(userId, couponId);

        // then
        assertThat(result.getId()).isEqualTo(userCoupon.getId());
        assertThat(result.getUserId()).isEqualTo(userCoupon.getUserId());
        assertThat(result.getCouponId()).isEqualTo(userCoupon.getCouponId());
    }

    @DisplayName("보유한 쿠폰이 없으면 사용자 쿠폰 ID로 가져올 수 없다.")
    @Test
    void findUserCouponByIdWhenNotFound() {
        // given
        Long userCouponId = 1L;

        // when & then
        assertThatThrownBy(() -> couponRepository.findUserCouponById(userCouponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("보유한 쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("보유한 쿠폰을 사용자 쿠폰 ID로 가져온다.")
    @Test
    void findUserCouponById() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        couponRepository.save(userCoupon);

        // when
        UserCoupon result = couponRepository.findUserCouponById(userCoupon.getId());

        // then
        assertThat(result.getId()).isEqualTo(userCoupon.getId());
        assertThat(result.getUserId()).isEqualTo(userCoupon.getUserId());
        assertThat(result.getCouponId()).isEqualTo(userCoupon.getCouponId());
    }

    @DisplayName("보유한 쿠폰을 사용자 ID와 사용 가능 상태값들로 가져온다.")
    @Test
    void findByUserId() {
        // given
        Long userId = 1L;
        Long anotherUserId = 2L;

        Coupon coupon1 = Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        Coupon coupon2 = Coupon.create("쿠폰명2", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));

        List.of(coupon1, coupon2).forEach(couponRepository::save);

        UserCoupon targetUserCoupon = UserCoupon.builder()
            .userId(userId)
            .couponId(coupon1.getId())
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        UserCoupon anotherCoupon = UserCoupon.builder()
            .userId(anotherUserId)
            .couponId(coupon2.getId())
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();

        List.of(targetUserCoupon, anotherCoupon).forEach(couponRepository::save);

        // when
        List<CouponInfo.Coupon> result = couponRepository.findByUserId(userId);

        // then
        assertThat(result).hasSize(1)
            .extracting("couponId", "couponName", "discountRate", "issuedAt")
            .containsExactlyInAnyOrder(
                tuple(coupon1.getId(), coupon1.getName(), coupon1.getDiscountRate(), targetUserCoupon.getIssuedAt())
            );
    }

}