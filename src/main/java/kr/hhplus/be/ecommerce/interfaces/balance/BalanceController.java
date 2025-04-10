package kr.hhplus.be.ecommerce.interfaces.balance;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.application.balance.BalanceFacade;
import kr.hhplus.be.ecommerce.application.balance.BalanceResult;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceFacade balanceFacade;

    @GetMapping("/api/v1/users/{id}/balance")
    public ApiResponse<BalanceResponse.Balance> getBalance(@PathVariable("id") Long id) {
        BalanceResult.Balance balance = balanceFacade.getBalance(id);
        return ApiResponse.success(BalanceResponse.Balance.of(balance));
    }

    @PostMapping("/api/v1/users/{id}/balance/charge")
    public ApiResponse<Void> updateBalance(@PathVariable("id") Long id,
                                           @Valid @RequestBody BalanceRequest.Charge request) {
        balanceFacade.chargeBalance(request.toCriteria(id));
        return ApiResponse.success();
    }
}
