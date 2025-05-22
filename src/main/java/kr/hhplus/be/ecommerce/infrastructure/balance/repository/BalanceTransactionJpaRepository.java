package kr.hhplus.be.ecommerce.infrastructure.balance.repository;

import kr.hhplus.be.ecommerce.domain.balance.BalanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceTransactionJpaRepository extends JpaRepository<BalanceTransaction, Long> {
}
