package kr.hhplus.be.ecommerce.product.infrastructure.jpa;

import kr.hhplus.be.ecommerce.product.domain.product.Product;
import kr.hhplus.be.ecommerce.product.domain.product.ProductSellingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    List<Product> findBySellStatusIn(List<ProductSellingStatus> sellStatuses);

    List<Product> findByIdIn(List<Long> ids);
}
