package kr.hhplus.be.ecommerce.product.domain.stock;

public interface StockRepository {

    Stock save(Stock stock);

    Stock findByProductId(Long productId);

    Stock findByProductIdWithLock(Long productId);
}
