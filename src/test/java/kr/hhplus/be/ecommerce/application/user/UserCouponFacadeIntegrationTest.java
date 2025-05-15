package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import kr.hhplus.be.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.be.ecommerce.domain.coupon.CouponStatus;
import kr.hhplus.be.ecommerce.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static kr.hhplus.be.ecommerce.application.user.UserCouponConstant.MAX_PUBLISH_COUNT_PER_REQUEST;
import static kr.hhplus.be.ecommerce.domain.coupon.CouponStatus.FINISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@Transactional
class UserCouponFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private User user;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);

        coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);
    }

    @DisplayName("사용자 쿠폰을 발급을 요청한다.")
    @Test
    void publishRequestUserCoupon() {
        // given
        UserCouponCriteria.PublishRequest criteria = UserCouponCriteria.PublishRequest.of(user.getId(), coupon.getId());

        // when
        userCouponFacade.requestPublishUserCoupon(criteria);

        // then
        Double score = redisTemplate.opsForZSet().score(UserCouponKey.of(coupon.getId()).generate(), user.getId());
        assertThat(score).isGreaterThan(LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.UTC));
    }

    @DisplayName("사용자 쿠폰을 발급한다.")
    @Test
    void publishUserCoupons() {
        // given
        for (long i = 1; i <= MAX_PUBLISH_COUNT_PER_REQUEST; i++) {
            UserCouponCriteria.PublishRequest criteria = UserCouponCriteria.PublishRequest.of(i, coupon.getId());
            userCouponFacade.requestPublishUserCoupon(criteria);
        }

        UserCouponCriteria.Publish criteria = UserCouponCriteria.Publish.of(MAX_PUBLISH_COUNT_PER_REQUEST);

        // when
        userCouponFacade.publishUserCoupons(criteria);

        // then
        int count = userCouponRepository.countByCouponId(coupon.getId());
        assertThat(count).isEqualTo(coupon.getQuantity());
    }

    @DisplayName("사용자 쿠폰 발급 시, 발급 수량이 최대 발급 개수를 초과하면 최대 발급 개수만큼 발급한다.")
    @Test
    void publishUserCouponExceedMaxPublishCountPerRequest() {
        // given
        Coupon coupon = Coupon.create("쿠폰명2", 0.2, 1000, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        for (long i = 1; i <= MAX_PUBLISH_COUNT_PER_REQUEST + 1; i++) {
            UserCouponCriteria.PublishRequest criteria = UserCouponCriteria.PublishRequest.of(i, coupon.getId());
            userCouponFacade.requestPublishUserCoupon(criteria);
        }

        UserCouponCriteria.Publish criteria = UserCouponCriteria.Publish.of(MAX_PUBLISH_COUNT_PER_REQUEST);

        // when
        userCouponFacade.publishUserCoupons(criteria);

        // then
        int count = userCouponRepository.countByCouponId(coupon.getId());
        assertThat(count).isEqualTo(MAX_PUBLISH_COUNT_PER_REQUEST);
    }

    @DisplayName("사용자 쿠폰 발급을 완료한다.")
    @Test
    void finishedPublishCoupons() {
        // given
        userCouponRepository.saveAll(List.of(
            UserCoupon.create(1L, coupon.getId()),
            UserCoupon.create(2L, coupon.getId()),
            UserCoupon.create(3L, coupon.getId()),
            UserCoupon.create(4L, coupon.getId()),
            UserCoupon.create(5L, coupon.getId()),
            UserCoupon.create(6L, coupon.getId()),
            UserCoupon.create(7L, coupon.getId()),
            UserCoupon.create(8L, coupon.getId()),
            UserCoupon.create(9L, coupon.getId()),
            UserCoupon.create(10L, coupon.getId())
        ));

        // when
        userCouponFacade.finishedPublishCoupons();

        // then
        assertThat(coupon.getStatus()).isEqualTo(FINISHED);
    }

    @DisplayName("보유 쿠폰 목록을 조회한다.")
    @Test
    void getUserCoupons() {
        // given
        Coupon anotherCoupon = Coupon.create("쿠폰명2", 0.2, 30, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(anotherCoupon);

        UserCoupon userCoupon1 = UserCoupon.create(user.getId(), coupon.getId());
        UserCoupon userCoupon2 = UserCoupon.create(user.getId(), anotherCoupon.getId());
        userCouponRepository.save(userCoupon1);
        userCouponRepository.save(userCoupon2);

        // when
        UserCouponResult.Coupons userCoupons = userCouponFacade.getUserCoupons(user.getId());

        // then
        assertThat(userCoupons.getCoupons()).hasSize(2)
            .extracting("userCouponId", "couponName", "discountRate")
            .containsExactlyInAnyOrder(
                tuple(userCoupon1.getId(), coupon.getName(), coupon.getDiscountRate()),
                tuple(userCoupon2.getId(), anotherCoupon.getName(), anotherCoupon.getDiscountRate())
            );
    }

}