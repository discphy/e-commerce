package kr.hhplus.be.ecommerce.application.balance;

import kr.hhplus.be.ecommerce.domain.balance.BalanceInfo;
import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceFacade {

    private final UserService userService;
    private final BalanceService balanceService;

    @Transactional
    public void chargeBalance(BalanceCriteria.Charge criteria) {
        userService.getUser(criteria.getUserId());
        balanceService.chargeBalance(criteria.toCommand());
    }

    @Transactional(readOnly = true)
    public BalanceResult.Balance getBalance(Long userId) {
        userService.getUser(userId);
        BalanceInfo.Balance balance = balanceService.getBalance(userId);
        return BalanceResult.Balance.of(balance.getAmount());
    }
}
