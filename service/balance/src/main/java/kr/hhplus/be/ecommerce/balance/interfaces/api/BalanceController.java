package kr.hhplus.be.ecommerce.balance.interfaces.api;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.balance.domain.BalanceInfo;
import kr.hhplus.be.ecommerce.balance.domain.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/api/v1/users/{id}/balance")
    public ApiResponse<BalanceResponse.Balance> getBalance(@PathVariable("id") Long id) {
        BalanceInfo.Balance balance = balanceService.getBalance(id);
        return ApiResponse.success(BalanceResponse.Balance.of(balance));
    }

    @PostMapping("/api/v1/users/{id}/balance/charge")
    public ApiResponse<Void> chargeBalance(@PathVariable("id") Long id,
                                           @Valid @RequestBody BalanceRequest.Charge request) {
        balanceService.chargeBalance(request.toCommand(id));
        return ApiResponse.success();
    }

    @PostMapping("/api/v1/users/{id}/balance/use")
    public ApiResponse<Void> useBalance(@PathVariable("id") Long id,
                                        @Valid @RequestBody BalanceRequest.Use request) {
        balanceService.useBalance(request.toCommand(id));
        return ApiResponse.success();
    }

    @PostMapping("/api/v1/users/{id}/balance/refund")
    public ApiResponse<Void> refundBalance(@PathVariable("id") Long id,
                                           @Valid @RequestBody BalanceRequest.Refund request) {
        balanceService.refundBalance(request.toCommand(id));
        return ApiResponse.success();
    }
}
