package kr.hhplus.be.ecommerce.infrastructure.balance.repository;

import kr.hhplus.be.ecommerce.domain.balance.Balance;
import kr.hhplus.be.ecommerce.domain.balance.BalanceRepository;
import kr.hhplus.be.ecommerce.domain.balance.BalanceTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BalanceRepositoryImpl implements BalanceRepository {

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
