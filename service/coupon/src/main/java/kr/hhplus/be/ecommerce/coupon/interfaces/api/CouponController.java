package kr.hhplus.be.ecommerce.coupon.interfaces.api;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.coupon.domain.CouponInfo;
import kr.hhplus.be.ecommerce.coupon.domain.CouponService;
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

    @GetMapping("/api/v1/coupons/{id}")
    public ApiResponse<CouponResponse.UserCoupon> getUsableCoupon(@PathVariable("id") Long id) {
        CouponInfo.Coupon usableCoupon = couponService.getUsableCoupon(id);
        return ApiResponse.success(CouponResponse.UserCoupon.of(usableCoupon));
    }

    @PutMapping("/api/v1/coupons/{id}/use")
    public ApiResponse<Void> useCoupon(@PathVariable("id") Long id) {
        couponService.useUserCoupon(id);
        return ApiResponse.success();
    }

    @PutMapping("/api/v1/coupons/{id}/cancel")
    public ApiResponse<Void> cancelCoupon(@PathVariable("id") Long id) {
        couponService.cancelUserCoupon(id);
        return ApiResponse.success();
    }
}
