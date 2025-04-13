package kr.hhplus.be.ecommerce.domain.product;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {

    Product findById(Long productId);

    List<Product> findSellingStatusIn(List<ProductSellingStatus> statuses);

    List<Product> findByIds(List<Long> productIds);
}
