package kr.hhplus.be.ecommerce.infrastructure.product;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Product findById(Long productId) {
        return productJpaRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
    }

    @Override
    public List<Product> findSellingStatusIn(List<ProductSellingStatus> statuses) {
        return productJpaRepository.findBySellStatusIn(statuses);
    }
}
