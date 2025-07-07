package kr.hhplus.be.ecommerce.balance.infrastructure.jpa;

import kr.hhplus.be.ecommerce.balance.domain.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);
}
