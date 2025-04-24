package kr.hhplus.be.ecommerce.infrastructure.stock;

import kr.hhplus.be.ecommerce.domain.stock.Stock;
import kr.hhplus.be.ecommerce.domain.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

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
}
