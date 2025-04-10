package kr.hhplus.be.ecommerce.domain.balance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;

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
                    throw new IllegalArgumentException("잔액이 부족합니다.");
                }
            );
    }
}
