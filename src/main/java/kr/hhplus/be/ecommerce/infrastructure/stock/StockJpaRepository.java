package kr.hhplus.be.ecommerce.infrastructure.stock;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.ecommerce.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Stock> findWithLockByProductId(Long productId);
}
