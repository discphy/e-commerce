package kr.hhplus.be.ecommerce.balance.infrastructure.jpa;

import kr.hhplus.be.ecommerce.balance.domain.BalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceTransactionJpaRepository extends JpaRepository<BalanceTransaction, Long> {
}
