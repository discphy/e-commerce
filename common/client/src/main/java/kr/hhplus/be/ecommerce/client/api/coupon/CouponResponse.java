package kr.hhplus.be.ecommerce.client.api.coupon;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {

    @Getter
    @NoArgsConstructor
    public static class UserCoupon {

        private Long userCouponId;
        private Long couponId;
        private String couponName;
        private double discountRate;
        private LocalDateTime issuedAt;

        @Builder
        private UserCoupon(Long userCouponId, Long couponId, String couponName, double discountRate, LocalDateTime issuedAt) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
            this.issuedAt = issuedAt;
        }
    }

}