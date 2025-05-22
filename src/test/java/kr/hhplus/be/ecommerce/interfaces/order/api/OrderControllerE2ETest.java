package kr.hhplus.be.ecommerce.interfaces.order.api;

import io.restassured.http.ContentType;
import kr.hhplus.be.ecommerce.domain.balance.Balance;
import kr.hhplus.be.ecommerce.domain.balance.BalanceRepository;
import kr.hhplus.be.ecommerce.domain.order.OrderClient;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.stock.Stock;
import kr.hhplus.be.ecommerce.domain.stock.StockRepository;
import kr.hhplus.be.ecommerce.domain.user.User;
import kr.hhplus.be.ecommerce.domain.user.UserRepository;
import kr.hhplus.be.ecommerce.test.support.E2EControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class OrderControllerE2ETest extends E2EControllerTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @MockitoBean
    private OrderClient orderClient;

    @DisplayName("주문/결제 시 - 잔액 부족")
    @Test
    void orderPaymentWithInsufficientBalance() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.create(user.getId());
        balanceRepository.save(balance);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, ProductSellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 200);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            null,
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1),
                OrderRequest.OrderProduct.of(product2.getId(), 2)
            )
        );

        Mockito.when(orderClient.getProducts(Mockito.any()))
            .thenReturn(
                List.of(
                    OrderInfo.Product.of(product1.getId(), product1.getName(), product1.getPrice(), 1),
                    OrderInfo.Product.of(product2.getId(), product2.getName(), product2.getPrice(), 2)
                )
            );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/orders")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"));
    }

    @DisplayName("주문/결제 시 - 재고 부족")
    @Test
    void orderPaymentWithInsufficientStock() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.create(user.getId());
        balance.charge(1_000_000L);
        balanceRepository.save(balance);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, ProductSellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 1);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            null,
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1),
                OrderRequest.OrderProduct.of(product2.getId(), 2)
            )
        );

        Mockito.when(orderClient.getProducts(Mockito.any()))
            .thenReturn(
                List.of(
                    OrderInfo.Product.of(product1.getId(), product1.getName(), product1.getPrice(), 1),
                    OrderInfo.Product.of(product2.getId(), product2.getName(), product2.getPrice(), 2)
                )
            );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/orders")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"));
    }

    @DisplayName("주문/결제 시 - 재고,잔액 부족")
    @Test
    void orderPaymentWithInsufficientStockAndBalance() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.create(user.getId());
        balanceRepository.save(balance);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, ProductSellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 1);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            null,
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1),
                OrderRequest.OrderProduct.of(product2.getId(), 2)
            )
        );

        Mockito.when(orderClient.getProducts(Mockito.any()))
            .thenReturn(
                List.of(
                    OrderInfo.Product.of(product1.getId(), product1.getName(), product1.getPrice(), 1),
                    OrderInfo.Product.of(product2.getId(), product2.getName(), product2.getPrice(), 2)
                )
            );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/orders")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"));
    }

    @DisplayName("주문/결제 한다.")
    @Test
    void orderPayment() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.create(user.getId());
        balance.charge(1_000_000L);
        balanceRepository.save(balance);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, ProductSellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 200);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            null,
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1),
                OrderRequest.OrderProduct.of(product2.getId(), 2)
            )
        );

        Mockito.when(orderClient.getProducts(Mockito.any()))
            .thenReturn(
                List.of(
                    OrderInfo.Product.of(product1.getId(), product1.getName(), product1.getPrice(), 1),
                    OrderInfo.Product.of(product2.getId(), product2.getName(), product2.getPrice(), 2)
                )
            );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/orders")
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"));
    }
}