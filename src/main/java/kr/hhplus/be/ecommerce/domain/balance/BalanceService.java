package kr.hhplus.be.ecommerce.domain.balance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private static final long EMPTY_BALANCE_AMOUNT = 0L;

    public void chargeBalance(BalanceCommand.Charge command) {
        balanceRepository.findOptionalByUserId(command.getUserId())
            .ifPresentOrElse(
                balance -> balance.charge(command.getAmount()),
                () -> {
                    Balance balance = Balance.create(command.getUserId(), command.getAmount());
                    balanceRepository.save(balance);
                }
            );
    }

    public void useBalance(BalanceCommand.Use command) {
        balanceRepository.findOptionalByUserId(command.getUserId())
            .ifPresentOrElse(
                balance -> balance.use(command.getAmount()),
                () -> {
                    throw new IllegalArgumentException("잔고가 존재하지 않습니다.");
                }
            );
    }

    public BalanceInfo.Balance getBalance(Long userId) {
        Long amount = balanceRepository.findOptionalByUserId(userId)
            .map(Balance::getAmount)
            .orElse(EMPTY_BALANCE_AMOUNT);
        
        return BalanceInfo.Balance.of(amount);
    }
}
