package kr.hhplus.be.ecommerce.interfaces.user;

import kr.hhplus.be.ecommerce.application.user.UserCouponResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponResponse {

    @Getter
    @NoArgsConstructor
    public static class Coupons {

        private List<CouponV1> coupons;

        private Coupons(List<CouponV1> coupons) {
            this.coupons = coupons;
        }

        public static Coupons of(UserCouponResult.Coupons coupons) {
            return new Coupons(coupons.getCoupons().stream()
                .map(CouponV1::of)
                .toList()
            );
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CouponV1 {

        private Long id;
        private String name;
        private double discountRate;

        @Builder
        private CouponV1(Long id, String name, double discountRate) {
            this.id = id;
            this.name = name;
            this.discountRate = discountRate;
        }

        public static CouponV1 of(UserCouponResult.Coupon coupon) {
            return CouponV1.builder()
                .id(coupon.getUserCouponId())
                .name(coupon.getCouponName())
                .discountRate(coupon.getDiscountRate())
                .build();
        }
    }

}