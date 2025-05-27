package kr.hhplus.be.ecommerce.interfaces.coupon.api;

import kr.hhplus.be.ecommerce.domain.coupon.CouponInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}