package kr.hhplus.be.ecommerce.coupon.infrastructure.redis;

import kr.hhplus.be.ecommerce.coupon.domain.CouponAvailableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCouponRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public boolean findPublishableCouponById(Long couponId) {
        CouponAvailableKey key = CouponAvailableKey.of(couponId);

        return Optional.ofNullable(redisTemplate.opsForValue().get(key.generate()))
            .map(Boolean::parseBoolean)
            .orElse(Boolean.FALSE);
    }

    public void updateAvailable(Long couponId, boolean available) {
        CouponAvailableKey key = CouponAvailableKey.of(couponId);
        redisTemplate.opsForValue().set(key.generate(), String.valueOf(available));
    }
}
