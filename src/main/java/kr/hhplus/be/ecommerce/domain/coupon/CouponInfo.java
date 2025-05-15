package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponInfo {

    @Getter
    public static class Coupon {

        private final Long couponId;
        private final String name;
        private final double discountRate;

        @Builder
        private Coupon(Long couponId, String name, double discountRate) {
            this.couponId = couponId;
            this.name = name;
            this.discountRate = discountRate;
        }
    }

    @Getter
    public static class PublishableCoupons {

        private final List<PublishableCoupon> coupons;

        private PublishableCoupons(List<PublishableCoupon> coupons) {
            this.coupons = coupons;
        }

        public static PublishableCoupons of(List<PublishableCoupon> coupons) {
            return new PublishableCoupons(coupons);
        }
    }

    @Getter
    public static class PublishableCoupon {

        private final Long couponId;
        private final int quantity;

        @Builder
        private PublishableCoupon(Long couponId, int quantity) {
            this.couponId = couponId;
            this.quantity = quantity;
        }

        public static PublishableCoupon of(Long couponId, int quantity) {
            return new PublishableCoupon(couponId, quantity);
        }
    }
}
