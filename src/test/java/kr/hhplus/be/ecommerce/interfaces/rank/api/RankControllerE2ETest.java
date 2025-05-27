package kr.hhplus.be.ecommerce.interfaces.rank.api;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.rank.Rank;
import kr.hhplus.be.ecommerce.domain.rank.RankRepository;
import kr.hhplus.be.ecommerce.test.support.E2EControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class RankControllerE2ETest extends E2EControllerTestSupport {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RankRepository rankRepository;

    @DisplayName("상위 상품 Top5 목록을 가져온다.")
    @Test
    void getRanks() {
        // given
        Product product1 = Product.create("항해 블랙뱃지", 100_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, ProductSellingStatus.SELLING);
        Product product3 = Product.create("항해 블루뱃지", 300_000L, ProductSellingStatus.SELLING);
        Product product4 = Product.create("항해 레드뱃지", 400_000L, ProductSellingStatus.SELLING);
        Product product5 = Product.create("항해 그린뱃지", 500_000L, ProductSellingStatus.SELLING);
        List.of(product1, product2, product3, product4, product5).forEach(productRepository::save);

        Rank rank1 = Rank.createSell(product1.getId(), LocalDate.now().minusDays(3), 10);
        Rank rank2 = Rank.createSell(product2.getId(), LocalDate.now().minusDays(2), 20);
        Rank rank3 = Rank.createSell(product3.getId(), LocalDate.now().minusDays(1), 30);
        Rank rank4 = Rank.createSell(product4.getId(), LocalDate.now().minusDays(1), 40);
        Rank rank5 = Rank.createSell(product5.getId(), LocalDate.now().minusDays(1), 50);
        List.of(rank1, rank2, rank3, rank4, rank5).forEach(rankRepository::save);

        // when & then
        given()
        .when()
            .get("/api/v1/products/ranks")
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"))
            .body("data.products[0].id", equalTo(product5.getId().intValue()))
            .body("data.products[0].name", equalTo(product5.getName()))
            .body("data.products[0].price", equalTo(500_000))
            .body("data.products[1].id", equalTo(product4.getId().intValue()))
            .body("data.products[1].name", equalTo(product4.getName()))
            .body("data.products[1].price", equalTo(400_000))
            .body("data.products[2].id", equalTo(product3.getId().intValue()))
            .body("data.products[2].name", equalTo(product3.getName()))
            .body("data.products[2].price", equalTo(300_000))
            .body("data.products[3].id", equalTo(product2.getId().intValue()))
            .body("data.products[3].name", equalTo(product2.getName()))
            .body("data.products[3].price", equalTo(200_000))
            .body("data.products[4].id", equalTo(product1.getId().intValue()))
            .body("data.products[4].name", equalTo(product1.getName()))
            .body("data.products[4].price", equalTo(100_000));
    }
}