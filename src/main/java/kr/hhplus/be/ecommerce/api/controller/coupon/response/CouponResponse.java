package kr.hhplus.be.ecommerce.api.controller.coupon.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponResponse {

    private Long id;
    private String name;
    private double discountRate;
    private String expiredAt;

    @Builder
    private CouponResponse(Long id, String name, double discountRate, String expiredAt) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.expiredAt = expiredAt;
    }
}
