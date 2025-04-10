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
            .map(this::toOrderProductInfo)
            .toList();

        return ProductInfo.OrderProducts.of(orderProducts);
    }

    public ProductInfo.Products getSellingProducts() {
        List<ProductInfo.Product> products = productRepository.findSellingStatusIn(ProductSellingStatus.forSelling()).stream()
            .map(this::toProductInfo)
            .toList();

        return ProductInfo.Products.of(products);
    }

    public ProductInfo.Products getProducts(ProductCommand.Products command) {
        List<ProductInfo.Product> products = productRepository.findByIds(command.getProductIds()).stream()
            .map(this::toProductInfo)
            .toList();

        return ProductInfo.Products.of(products);
    }

    private ProductInfo.OrderProduct toOrderProductInfo(ProductCommand.OrderProduct command) {
        Product product = getProduct(command);

        return ProductInfo.OrderProduct.builder()
            .productId(product.getId())
            .productName(product.getName())
            .productPrice(product.getPrice())
            .quantity(command.getQuantity())
            .build();
    }

    private ProductInfo.Product toProductInfo(Product product) {
        return ProductInfo.Product.builder()
            .productId(product.getId())
            .productName(product.getName())
            .productPrice(product.getPrice())
            .build();
    }

    private Product getProduct(ProductCommand.OrderProduct command) {
        Product product = productRepository.findById(command.getProductId());

        if (product.cannotSelling()) {
            throw new IllegalStateException("주문 불가한 상품이 포함되어 있습니다.");
        }

        return product;
    }
}
