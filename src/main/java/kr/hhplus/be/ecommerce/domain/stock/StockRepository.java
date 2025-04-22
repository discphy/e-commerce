package kr.hhplus.be.ecommerce.domain.stock;

public interface StockRepository {

    Stock save(Stock stock);

    Stock findByProductId(Long productId);

    Stock findWithLockByProductId(Long productId);
}
