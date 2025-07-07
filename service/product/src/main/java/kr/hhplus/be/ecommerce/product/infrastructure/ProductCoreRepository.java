package kr.hhplus.be.ecommerce.product.infrastructure;

import kr.hhplus.be.ecommerce.product.domain.product.*;
import kr.hhplus.be.ecommerce.product.infrastructure.jpa.ProductJpaRepository;
import kr.hhplus.be.ecommerce.product.infrastructure.querydsl.ProductQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductCoreRepository implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryDslRepository productQueryDslRepository;

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
    public List<ProductInfo.Product> findBySellStatusIn(List<ProductSellingStatus> statuses) {
        return productQueryDslRepository.findBySellStatusIn(statuses);
    }

    @Override
    public List<ProductInfo.Product> findAll(ProductCommand.Query command) {
        return productQueryDslRepository.findAll(command);
    }
}
