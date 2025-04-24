package kr.hhplus.be.ecommerce.application.product;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.domain.order.Order;
import kr.hhplus.be.ecommerce.domain.order.OrderProduct;
import kr.hhplus.be.ecommerce.domain.order.OrderRepository;
import kr.hhplus.be.ecommerce.domain.payment.Payment;
import kr.hhplus.be.ecommerce.domain.payment.PaymentRepository;
import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.stock.Stock;
import kr.hhplus.be.ecommerce.domain.stock.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ProductFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Product product1;

    private Product product2;

    private Product product3;

    @BeforeEach
    void setUp() {
        product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.STOP_SELLING);

        List.of(product1, product2, product3)
            .forEach(productRepository::save);

        Stock stock1 = Stock.create(product1.getId(), 10);
        Stock stock2 = Stock.create(product2.getId(), 20);
        Stock stock3 = Stock.create(product3.getId(), 30);

        List.of(stock1, stock2, stock3)
            .forEach(stockRepository::save);

    }

    @DisplayName("판매 가능 상품 목록을 조회한다.")
    @Test
    void getProducts() {
        // when
        ProductResult.Products products = productFacade.getProducts();

        // then
        assertThat(products.getProducts()).hasSize(2)
            .extracting(ProductResult.Product::getProductId)
            .containsExactlyInAnyOrder(product1.getId(), product2.getId());
    }

    @DisplayName("상위 상품을 조회한다.")
    @Test
    void getPopularProducts() {
        // given
        Order order1 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(product1.getId(), "상품1", 10_000L, 2),
            OrderProduct.create(product2.getId(), "상품2", 20_000L, 3)
        ));
        Order order2 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(product1.getId(), "상품1", 10_000L, 2),
            OrderProduct.create(product3.getId(), "상품3", 30_000L, 4)
        ));
        Order order3 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(product2.getId(), "상품2", 20_000L, 3),
            OrderProduct.create(product3.getId(), "상품3", 30_000L, 4)
        ));
        List.of(order1, order2, order3).forEach(orderRepository::save);

        Payment payment1 = Payment.create(order1.getId(), 80_000L);
        Payment payment2 = Payment.create(order2.getId(), 140_000L);
        Payment payment3 = Payment.create(order3.getId(), 180_000L);
        List.of(payment1, payment2, payment3).forEach(payment -> {
            payment.pay();
            paymentRepository.save(payment);
        });

        // when
        ProductResult.Products products = productFacade.getPopularProducts();

        // then
        assertThat(products.getProducts()).hasSize(3)
            .extracting("productId")
            .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }
}