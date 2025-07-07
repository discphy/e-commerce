package kr.hhplus.be.ecommerce.product.infrastructure;

import kr.hhplus.be.ecommerce.product.domain.stock.Stock;
import kr.hhplus.be.ecommerce.product.domain.stock.StockRepository;
import kr.hhplus.be.ecommerce.product.infrastructure.jpa.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockCoreRepository implements StockRepository {

    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock save(Stock stock) {
        return stockJpaRepository.save(stock);
    }

    @Override
    public Stock findByProductId(Long productId) {
        return stockJpaRepository.findByProductId(productId)
            .orElseThrow(() -> new IllegalArgumentException("재고가 존재하지 않습니다."));
    }

    @Override
    public Stock findByProductIdWithLock(Long productId) {
        return stockJpaRepository.findByProductIdWithLock(productId)
            .orElseThrow(() -> new IllegalArgumentException("재고가 존재하지 않습니다."));
    }
}
