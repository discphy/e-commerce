package kr.hhplus.be.ecommerce.interfaces.balance.api;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.domain.balance.BalanceInfo;
import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
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
}
