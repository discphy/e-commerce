package kr.hhplus.be.ecommerce.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponCommand {

    @Getter
    public static class Publish {

        private final Long userId;
        private final Long couponId;

        private Publish(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static Publish of(Long userId, Long couponId) {
            return new Publish(userId, couponId);
        }
    }

    @Getter
    public static class UsableCoupon {

        private final Long userId;
        private final Long couponId;

        private UsableCoupon(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static UsableCoupon of(Long userId, Long couponId) {
            return new UsableCoupon(userId, couponId);
        }
    }
}
