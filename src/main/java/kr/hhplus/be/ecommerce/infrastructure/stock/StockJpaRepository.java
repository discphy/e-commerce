package kr.hhplus.be.ecommerce.infrastructure.stock;

import kr.hhplus.be.ecommerce.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);
}
