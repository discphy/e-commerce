package kr.hhplus.be.ecommerce.domain.product;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductRepository {

    Product save(Product product);

    Product findById(Long productId);

    List<Product> findSellingStatusIn(List<ProductSellingStatus> statuses);

    List<Product> findByIds(List<Long> productIds);
}
