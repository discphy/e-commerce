package kr.hhplus.be.ecommerce.infrastructure.stock;

import kr.hhplus.be.ecommerce.domain.stock.Stock;
import kr.hhplus.be.ecommerce.domain.stock.StockRepository;
import org.springframework.stereotype.Component;

@Component
public class StockRepositoryImpl implements StockRepository {

    @Override
    public Stock findByProductId(Long productId) {
        return null;
    }
}
