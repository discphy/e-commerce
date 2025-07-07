package kr.hhplus.be.ecommerce.client.api.coupon;

import kr.hhplus.be.ecommerce.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "coupon-service", url = "${endpoints.coupon-service.url}")
public interface CouponApiClient {

    @GetMapping("/api/v1/coupons/{id}")
    ApiResponse<CouponResponse.UserCoupon> getUsableCoupon(@PathVariable("id") Long id);

    @PutMapping("/api/v1/coupons/{id}/use")
    ApiResponse<Void> useCoupon(@PathVariable("id") Long id);

    @PutMapping("/api/v1/coupons/{id}/cancel")
    ApiResponse<Void> cancelCoupon(@PathVariable("id") Long id);
}
