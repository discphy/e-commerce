package kr.hhplus.be.ecommerce.interfaces.product.api;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.stock.Stock;
import kr.hhplus.be.ecommerce.domain.stock.StockRepository;
import kr.hhplus.be.ecommerce.test.support.E2EControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class ProductControllerE2ETest extends E2EControllerTestSupport {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("상품 목록을 가져온다.")
    @Test
    void getProducts() {
        // given
        Product product1 = Product.create("항해 블랙뱃지", 100_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, ProductSellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 200);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        // when & then
        given()
        .when()
            .get("/api/v1/products")
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"))
            .body("data.products[0].id", equalTo(product1.getId().intValue()))
            .body("data.products[0].name", equalTo(product1.getName()))
            .body("data.products[0].price", equalTo(100_000))
            .body("data.products[0].stock", equalTo(stock1.getQuantity()))
            .body("data.products[1].id", equalTo(product2.getId().intValue()))
            .body("data.products[1].name", equalTo(product2.getName()))
            .body("data.products[1].price", equalTo(200_000))
            .body("data.products[1].stock", equalTo(stock2.getQuantity()));
    }
}