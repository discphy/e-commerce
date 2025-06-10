package kr.hhplus.be.ecommerce.domain.product;

import kr.hhplus.be.ecommerce.domain.stock.Stock;
import kr.hhplus.be.ecommerce.domain.stock.StockRepository;
import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
class ProductServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("주문에 필요한 상품 정보를 가져올 시, 주문 불가한 상품이 있다면 예외가 발생한다.")
    @Test
    void getOrderProductsIfFindAnyCannotSellingStatus() {
        // given
        Product product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        Product product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.SELLING);
        Product cannotSellingProduct = Product.create("상품명4", 4_000L, ProductSellingStatus.STOP_SELLING);
        List.of(product1, product2, product3, cannotSellingProduct).forEach(productRepository::save);

        List<ProductCommand.OrderProduct> orderProducts = List.of(
            ProductCommand.OrderProduct.of(product1.getId(), 1),
            ProductCommand.OrderProduct.of(product2.getId(), 2),
            ProductCommand.OrderProduct.of(product3.getId(), 3),
            ProductCommand.OrderProduct.of(cannotSellingProduct.getId(), 4)
        );

        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(orderProducts);

        // when & then
        assertThatThrownBy(() -> productService.getOrderProducts(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("주문 불가한 상품이 포함되어 있습니다.");
    }

    @DisplayName("주문에 필요한 상품 정보를 가져온다.")
    @Test
    void getOrderProducts() {
        // given
        Product product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        Product product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.SELLING);
        List.of(product1, product2, product3).forEach(productRepository::save);

        List<ProductCommand.OrderProduct> orderProducts = List.of(
            ProductCommand.OrderProduct.of(product1.getId(), 1),
            ProductCommand.OrderProduct.of(product2.getId(), 2),
            ProductCommand.OrderProduct.of(product3.getId(), 3)
        );

        ProductCommand.OrderProducts command = ProductCommand.OrderProducts.of(orderProducts);

        // when
        ProductInfo.OrderProducts result = productService.getOrderProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(3)
            .extracting("productId", "productName", "productPrice", "quantity")
            .containsExactly(
                tuple(product1.getId(), product1.getName(), product1.getPrice(), 1),
                tuple(product2.getId(), product2.getName(), product2.getPrice(), 2),
                tuple(product3.getId(), product3.getName(), product3.getPrice(), 3)
            );
    }

    @DisplayName("판매중인 상품을 조회한다.")
    @Test
    void getSellingProducts() {
        // given
        Product product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        Product product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.SELLING);
        List.of(product1, product2, product3).forEach(productRepository::save);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 200);
        Stock stock3 = Stock.create(product3.getId(), 300);
        List.of(stock1, stock2, stock3).forEach(stockRepository::save);

        // when
        ProductInfo.Products result = productService.getSellingProducts();

        // then
        assertThat(result.getProducts()).hasSize(3)
            .extracting("productId", "productName", "productPrice", "quantity")
            .containsExactly(
                tuple(product1.getId(), product1.getName(), product1.getPrice(), stock1.getQuantity()),
                tuple(product2.getId(), product2.getName(), product2.getPrice(), stock2.getQuantity()),
                tuple(product3.getId(), product3.getName(), product3.getPrice(), stock3.getQuantity())
            );
    }

    @DisplayName("상품 ID 리스트로 상품 정보를 조회한다.")
    @Test
    void getProducts() {
        // given
        Product product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        Product product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.SELLING);
        List.of(product1, product2, product3).forEach(productRepository::save);

        ProductCommand.Products command = ProductCommand.Products.of(List.of(product1.getId(), product2.getId(), product3.getId()));

        // when
        ProductInfo.Products result = productService.getProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(3)
            .extracting("productId", "productName", "productPrice")
            .containsExactly(
                tuple(product1.getId(), product1.getName(), product1.getPrice()),
                tuple(product2.getId(), product2.getName(), product2.getPrice()),
                tuple(product3.getId(), product3.getName(), product3.getPrice())
            );
    }

    @DisplayName("커서 없이 상품 목록을 페이징 조회한다.")
    @Test
    void getProductsWithoutCursor() {
        // given
        Product product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        Product product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.SELLING);
        List.of(product1, product2, product3).forEach(productRepository::save);

        ProductCommand.Query command = ProductCommand.Query.of(2L, null);

        // when
        ProductInfo.Products result = productService.getProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(2)
            .extracting("productId", "productName", "productPrice")
            .containsExactly(
                tuple(product3.getId(), product3.getName(), product3.getPrice()),
                tuple(product2.getId(), product2.getName(), product2.getPrice())
            );
    }

    @DisplayName("커서로 상품 목록을 페이징 조회한다.")
    @Test
    void getProductsWithCursor() {
        // given
        Product product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        Product product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.SELLING);
        List.of(product1, product2, product3).forEach(productRepository::save);

        ProductCommand.Query command = ProductCommand.Query.of(2L, product2.getId());

        // when
        ProductInfo.Products result = productService.getProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(1)
            .extracting("productId", "productName", "productPrice")
            .containsExactly(
                tuple(product1.getId(), product1.getName(), product1.getPrice())
            );
    }

}