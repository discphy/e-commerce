package kr.hhplus.be.ecommerce.balance.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceClient balanceClient;
    private final BalanceRepository balanceRepository;
    private static final long EMPTY_BALANCE_AMOUNT = 0L;

    @Transactional
    public void chargeBalance(BalanceCommand.Charge command) {
        balanceClient.getUser(command.getUserId());

        Balance balance = balanceRepository.findOptionalByUserId(command.getUserId())
            .orElseGet(() -> balanceRepository.save(Balance.create(command.getUserId())));

        balance.charge(command.getAmount());

        BalanceTransaction transaction = BalanceTransaction.ofCharge(balance, command.getAmount());
        balanceRepository.saveTransaction(transaction);
    }

    @Transactional
    public void useBalance(BalanceCommand.Use command) {
        Balance balance = balanceRepository.findOptionalByUserId(command.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("잔고가 존재하지 않습니다."));

        balance.use(command.getAmount());

        BalanceTransaction transaction = BalanceTransaction.ofUse(balance, command.getAmount());
        balanceRepository.saveTransaction(transaction);
    }

    @Transactional
    public void refundBalance(BalanceCommand.Refund command) {
        Balance balance = balanceRepository.findOptionalByUserId(command.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("잔고가 존재하지 않습니다."));

        balance.refund(command.getAmount());

        BalanceTransaction transaction = BalanceTransaction.ofRefund(balance, command.getAmount());
        balanceRepository.saveTransaction(transaction);
    }

    @Transactional(readOnly = true)
    public BalanceInfo.Balance getBalance(Long userId) {
        balanceClient.getUser(userId);

        Long amount = balanceRepository.findOptionalByUserId(userId)
            .map(Balance::getAmount)
            .orElse(EMPTY_BALANCE_AMOUNT);

        return BalanceInfo.Balance.of(amount);
    }
}
