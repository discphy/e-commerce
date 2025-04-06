package kr.hhplus.be.ecommerce.api.controller.coupon.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CouponsResponse {

    private List<CouponResponse> coupons;

    private CouponsResponse(List<CouponResponse> coupons) {
        this.coupons = coupons;
    }

    public static CouponsResponse of(List<CouponResponse> coupons) {
        return new CouponsResponse(coupons);
    }

}
