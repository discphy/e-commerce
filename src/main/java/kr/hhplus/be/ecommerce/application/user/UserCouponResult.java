package kr.hhplus.be.ecommerce.application.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponResult {

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
        private final String couponName;
        private final double discountRate;

        @Builder
        private Coupon(Long userCouponId, String couponName, double discountRate) {
            this.userCouponId = userCouponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
        }

        public static Coupon of(Long userCouponId, String couponName, double discountRate) {
            return Coupon.builder()
                .userCouponId(userCouponId)
                .couponName(couponName)
                .discountRate(discountRate)
                .build();
        }
    }
}
