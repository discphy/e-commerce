package kr.hhplus.be.ecommerce.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponInfo {

    @Getter
    public static class UsableCoupon {

        private final Long userCouponId;

        private UsableCoupon(Long userCouponId) {
            this.userCouponId = userCouponId;
        }

        public static UsableCoupon of(Long userCouponId) {
            return new UsableCoupon(userCouponId);
        }
    }
}
