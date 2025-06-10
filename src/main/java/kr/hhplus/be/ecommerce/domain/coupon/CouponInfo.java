package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponInfo {

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
        private final String couponName;
        private final double discountRate;
        private final LocalDateTime issuedAt;

        @Builder
        public Coupon(Long userCouponId, Long couponId, String couponName, double discountRate, LocalDateTime issuedAt) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
            this.issuedAt = issuedAt;
        }
    }

    @Getter
    public static class User {

        private final Long id;
        private final String userName;

        private User(Long id, String userName) {
            this.id = id;
            this.userName = userName;
        }

        public static User of(Long userId, String userName) {
            return new User(userId, userName);
        }
    }
}
