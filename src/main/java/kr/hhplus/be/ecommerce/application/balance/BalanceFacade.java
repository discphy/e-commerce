package kr.hhplus.be.ecommerce.application.balance;

import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceFacade {

    private final UserService userService;
    private final BalanceService balanceService;

    public void chargeBalance(BalanceCriteria.Charge criteria) {
        userService.getUser(criteria.getUserId());
        balanceService.chargeBalance(criteria.toCommand());
    }
}
