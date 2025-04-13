package kr.hhplus.be.ecommerce.domain.balance;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository {

    Optional<Balance> findOptionalByUserId(Long userId);

    Balance save(Balance balance);
}
