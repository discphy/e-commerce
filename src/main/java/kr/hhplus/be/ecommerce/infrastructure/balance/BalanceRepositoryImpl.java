package kr.hhplus.be.ecommerce.infrastructure.balance;

import kr.hhplus.be.ecommerce.domain.balance.Balance;
import kr.hhplus.be.ecommerce.domain.balance.BalanceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BalanceRepositoryImpl implements BalanceRepository {

    @Override
    public Optional<Balance> findOptionalByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public Balance save(Balance balance) {
        return null;
    }
}
