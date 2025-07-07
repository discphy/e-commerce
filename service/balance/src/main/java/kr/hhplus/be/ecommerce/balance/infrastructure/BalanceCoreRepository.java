package kr.hhplus.be.ecommerce.balance.infrastructure;

import kr.hhplus.be.ecommerce.balance.domain.Balance;
import kr.hhplus.be.ecommerce.balance.domain.BalanceRepository;
import kr.hhplus.be.ecommerce.balance.domain.BalanceTransaction;
import kr.hhplus.be.ecommerce.balance.infrastructure.jpa.BalanceJpaRepository;
import kr.hhplus.be.ecommerce.balance.infrastructure.jpa.BalanceTransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BalanceCoreRepository implements BalanceRepository {

    private final BalanceJpaRepository balanceJpaRepository;
    private final BalanceTransactionJpaRepository balanceTransactionJpaRepository;

    @Override
    public Optional<Balance> findOptionalByUserId(Long userId) {
        return balanceJpaRepository.findByUserId(userId);
    }

    @Override
    public Balance save(Balance balance) {
        return balanceJpaRepository.save(balance);
    }

    @Override
    public BalanceTransaction saveTransaction(BalanceTransaction transaction) {
        return balanceTransactionJpaRepository.save(transaction);
    }
}
