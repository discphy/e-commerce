package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.domain.user.UserCouponKey;
import kr.hhplus.be.ecommerce.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

class UserCouponFacadeConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @DisplayName("동시에 선착순 발급 요청 시, 모든 요청에 대해 발급 요청이 되어야 한다.")
    @Test
    void requestPublishUserCoupon() {
        // given
        Long couponId = 1000L;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(LongStream.range(0, 3000)
            .mapToObj(
                userId -> (Runnable) () -> {
                    try {
                        userCouponFacade.requestPublishUserCoupon(UserCouponCriteria.PublishRequest.of(userId, couponId));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        e.printStackTrace();
                        failCount.incrementAndGet();
                    }
                }
            ).toList()
        );

        // then
        assertThat(successCount.get()).isEqualTo(3000);
        assertThat(failCount.get()).isZero();

        Long size = redisTemplate.opsForZSet().size(UserCouponKey.of(couponId).generate());
        assertThat(size).isEqualTo(3000);
    }

    @DisplayName("동시에 선착순 발급 시, 중복으로 발급 요청할 수 없다.")
    @Test
    void cannotDuplicateRequestPublishUserCoupon() {
        // given
        Long userId = 1L;
        Long couponId = 1000L;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(List.of(
            () -> {
                try {
                    userCouponFacade.requestPublishUserCoupon(UserCouponCriteria.PublishRequest.of(userId, couponId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            },
            () -> {
                try {
                    userCouponFacade.requestPublishUserCoupon(UserCouponCriteria.PublishRequest.of(userId, couponId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            }
        ));

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Long size = redisTemplate.opsForZSet().size(UserCouponKey.of(couponId).generate());
        assertThat(size).isEqualTo(1);
    }
}