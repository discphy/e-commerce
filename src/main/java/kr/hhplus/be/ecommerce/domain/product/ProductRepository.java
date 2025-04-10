package kr.hhplus.be.ecommerce.domain.product;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository {

    Product findById(Long productId);
}
