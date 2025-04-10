package kr.hhplus.be.ecommerce.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @Getter
    public static class Coupons {

        private final List<Coupon> coupons;

        private Coupons(List<Coupon> coupons) {
            this.coupons = coupons;
        }

        public static Coupons of(List<Coupon> coupons) {
            return new Coupons(coupons);
        }
    }

    @Getter
    public static class Coupon {

        private final Long userCouponId;
        private final Long couponId;
        private final LocalDateTime issuedAt;

        @Builder
        private Coupon(Long userCouponId, Long couponId, LocalDateTime issuedAt) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.issuedAt = issuedAt;
        }
    }
}
