package kr.hhplus.be.ecommerce.interfaces.coupon.api;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.domain.coupon.CouponInfo;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/api/v1/users/{id}/coupons")
    public ApiResponse<CouponResponse.Coupons> getCoupons(@PathVariable("id") Long id) {
        CouponInfo.Coupons userCoupons = couponService.getUserCoupons(id);
        return ApiResponse.success(CouponResponse.Coupons.of(userCoupons));
    }

    @PostMapping("/api/v1/users/{id}/coupons/publish")
    public ApiResponse<Void> publishCoupon(@PathVariable("id") Long id,
                                           @Valid @RequestBody CouponRequest.Publish request) {
        couponService.requestPublishUserCoupon(request.toCommand(id));
        return ApiResponse.success();
    }
}
