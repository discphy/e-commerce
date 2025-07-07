package kr.hhplus.be.ecommerce.coupon.interfaces.api;

import kr.hhplus.be.ecommerce.coupon.domain.CouponInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponResponse {

    @Getter
    @NoArgsConstructor
    public static class Coupons {

        private List<Coupon> coupons;

        private Coupons(List<Coupon> coupons) {
            this.coupons = coupons;
        }

        public static Coupons of(CouponInfo.Coupons userCoupons) {
            return new Coupons(userCoupons.getCoupons().stream()
                .map(Coupon::of)
                .toList()
            );
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Coupon {

        private Long id;
        private String name;
        private double discountRate;

        @Builder
        private Coupon(Long id, String name, double discountRate) {
            this.id = id;
            this.name = name;
            this.discountRate = discountRate;
        }

        public static Coupon of(CouponInfo.Coupon coupon) {
            return Coupon.builder()
                .id(coupon.getUserCouponId())
                .name(coupon.getCouponName())
                .discountRate(coupon.getDiscountRate())
                .build();
        }
    }

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

        public static UserCoupon of(CouponInfo.Coupon coupon) {
            return UserCoupon.builder()
                .couponId(coupon.getCouponId())
                .userCouponId(coupon.getUserCouponId())
                .couponName(coupon.getCouponName())
                .discountRate(coupon.getDiscountRate())
                .issuedAt(coupon.getIssuedAt())
                .build();
        }
    }

}