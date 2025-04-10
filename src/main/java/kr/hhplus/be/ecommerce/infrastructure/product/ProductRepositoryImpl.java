package kr.hhplus.be.ecommerce.infrastructure.product;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public Product findById(Long productId) {
        return null;
    }
}
