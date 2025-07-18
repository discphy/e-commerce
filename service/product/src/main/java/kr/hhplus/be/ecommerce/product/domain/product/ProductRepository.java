package kr.hhplus.be.ecommerce.product.domain.product;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductRepository {

    Product save(Product product);

    Product findById(Long productId);

    List<ProductInfo.Product> findBySellStatusIn(List<ProductSellingStatus> statuses);

    List<ProductInfo.Product> findAll(ProductCommand.Query command);
}
