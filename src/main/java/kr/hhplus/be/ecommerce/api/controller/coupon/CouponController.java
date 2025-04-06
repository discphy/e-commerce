package kr.hhplus.be.ecommerce.api.controller.coupon;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.api.controller.ApiResponse;
import kr.hhplus.be.ecommerce.api.controller.coupon.request.CouponPublishRequest;
import kr.hhplus.be.ecommerce.api.controller.coupon.response.CouponResponse;
import kr.hhplus.be.ecommerce.api.controller.coupon.response.CouponsResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CouponController {

    @GetMapping("/api/v1/users/{id}/coupons")
    public ApiResponse<CouponsResponse> getCoupons(@PathVariable("id") Long id) {
        return ApiResponse.success(CouponsResponse.of(mockCoupons()));
    }

    @PostMapping("/api/v1/users/{id}/coupons")
    public ApiResponse<Void> publishCoupon(@PathVariable("id") Long id,
                                           @Valid @RequestBody CouponPublishRequest request) {
        return ApiResponse.success();
    }

    private List<CouponResponse> mockCoupons() {
        return List.of(
            CouponResponse.builder()
                .id(1L)
                .name("쿠폰명")
                .discountRate(0.1)
                .expiredAt("2025-04-01 12:00:00")
                .build()
        );
    }
}
