package kr.hhplus.be.ecommerce.infrastructure.product;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public Product findById(Long productId) {
        return null;
    }

    @Override
    public List<Product> findSellingStatusIn(List<ProductSellingStatus> statuses) {
        return List.of();
    }
}
