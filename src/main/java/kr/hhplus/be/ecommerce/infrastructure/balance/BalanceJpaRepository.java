package kr.hhplus.be.ecommerce.infrastructure.balance;

import kr.hhplus.be.ecommerce.domain.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);
}
