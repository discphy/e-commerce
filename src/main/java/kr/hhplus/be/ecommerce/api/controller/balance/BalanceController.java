package kr.hhplus.be.ecommerce.api.controller.balance;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.api.controller.ApiResponse;
import kr.hhplus.be.ecommerce.api.controller.balance.request.BalanceUpdateRequest;
import kr.hhplus.be.ecommerce.api.controller.balance.response.BalanceResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class BalanceController {

    @GetMapping("/api/v1/users/{id}/balance")
    public ApiResponse<BalanceResponse> getBalance(@PathVariable("id") Long id) {
        return ApiResponse.success(BalanceResponse.of(1000L));
    }

    @PostMapping("/api/v1/users/{id}/balance")
    public ApiResponse<Void> updateBalance(@PathVariable("id") Long id,
                                           @Valid @RequestBody BalanceUpdateRequest request) {
        return ApiResponse.success();
    }
}
