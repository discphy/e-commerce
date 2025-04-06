package kr.hhplus.be.ecommerce.api.controller.coupon.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponPublishRequest {

    @NotNull(message = "쿠폰 ID는 필수입니다.")
    private Long couponId;

    private CouponPublishRequest(Long couponId) {
        this.couponId = couponId;
    }

    public static CouponPublishRequest of(Long couponId) {
        return new CouponPublishRequest(couponId);
    }
}
