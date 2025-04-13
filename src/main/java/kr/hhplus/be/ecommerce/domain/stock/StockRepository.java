package kr.hhplus.be.ecommerce.domain.stock;

public interface StockRepository {

    Stock findByProductId(Long productId);
}
