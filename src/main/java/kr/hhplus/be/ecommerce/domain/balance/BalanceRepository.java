package kr.hhplus.be.ecommerce.domain.balance;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface BalanceRepository {

    Optional<Balance> findOptionalByUserId(Long userId);

    Balance save(Balance balance);

    BalanceTransaction saveTransaction(BalanceTransaction balanceTransaction);
}
