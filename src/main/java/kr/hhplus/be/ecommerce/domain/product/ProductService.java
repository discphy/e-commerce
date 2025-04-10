package kr.hhplus.be.ecommerce.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductInfo.OrderProducts getOrderProducts(ProductCommand.OrderProducts command) {
        List<ProductInfo.OrderProduct> orderProducts = command.getProducts().stream()
            .map(this::getOrderProduct)
            .toList();

        return ProductInfo.OrderProducts.of(orderProducts);
    }

    public ProductInfo.Products getSellingProducts() {
        return ProductInfo.Products.of(
            productRepository.findSellingStatusIn(ProductSellingStatus.forCelling())
                .stream()
                .map(product -> ProductInfo.Product.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productPrice(product.getPrice())
                    .build()
                ).toList()
        );
    }

    private ProductInfo.OrderProduct getOrderProduct(ProductCommand.OrderProduct command) {
        Product product = productRepository.findById(command.getProductId());

        if (product.cannotSelling()) {
            throw new IllegalStateException("주문 불가한 상품이 포함되어 있습니다.");
        }

        return ProductInfo.OrderProduct.builder()
            .productId(product.getId())
            .productName(product.getName())
            .productPrice(product.getPrice())
            .quantity(command.getQuantity())
            .build();
    }
}
