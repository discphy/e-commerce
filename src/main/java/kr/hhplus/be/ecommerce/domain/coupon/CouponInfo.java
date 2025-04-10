package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
