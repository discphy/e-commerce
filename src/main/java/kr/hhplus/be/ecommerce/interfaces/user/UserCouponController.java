package kr.hhplus.be.ecommerce.interfaces.user;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.application.user.UserCouponFacade;
import kr.hhplus.be.ecommerce.application.user.UserCouponResult;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponFacade userCouponFacade;

    @GetMapping("/api/v1/users/{id}/coupons")
    public ApiResponse<UserCouponResponse.Coupons> getCoupons(@PathVariable("id") Long id) {
        UserCouponResult.Coupons userCoupons = userCouponFacade.getUserCoupons(id);
        return ApiResponse.success(UserCouponResponse.Coupons.of(userCoupons));
    }

    @PostMapping("/api/v1/users/{id}/coupons/publish")
    public ApiResponse<Void> publishCoupon(@PathVariable("id") Long id,
                                           @Valid @RequestBody UserCouponRequest.Publish request) {
        userCouponFacade.publishUserCoupon(request.toCriteria(id));
        return ApiResponse.success();
    }
}
