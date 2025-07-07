package kr.hhplus.be.ecommerce.coupon.interfaces.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import kr.hhplus.be.ecommerce.coupon.domain.*;
import kr.hhplus.be.ecommerce.coupon.support.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.coupon.support.database.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class CouponControllerE2ETest extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private CouponRepository couponRepository;

    @MockitoBean
    private CouponClient couponClient;

    private CouponInfo.User user;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.clean();

        user = CouponInfo.User.of(1L, "항플");
        Mockito.when(couponClient.getUser(user.getId()))
            .thenReturn(user);
    }

    @DisplayName("보유한 쿠폰 목록을 가져온다.")
    @Test
    void getCoupons() {
        // given
        Coupon coupon1 = Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        Coupon coupon2 = Coupon.create("쿠폰명2", 0.2, 20, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon1);
        couponRepository.save(coupon2);

        UserCoupon userCoupon1 = UserCoupon.create(user.getId(), coupon1.getId());
        UserCoupon userCoupon2 = UserCoupon.create(user.getId(), coupon2.getId());
        couponRepository.save(userCoupon1);
        couponRepository.save(userCoupon2);

        // when
        given()
        .when()
            .get("/api/v1/users/{id}/coupons", user.getId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"))
            .body("data.coupons[0].id", equalTo(userCoupon1.getId().intValue()))
            .body("data.coupons[0].name", equalTo(coupon1.getName()))
            .body("data.coupons[0].discountRate", equalTo(0.1f))
            .body("data.coupons[1].id", equalTo(userCoupon2.getId().intValue()))
            .body("data.coupons[1].name", equalTo(coupon2.getName()))
            .body("data.coupons[1].discountRate", equalTo(0.2f));
    }

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void publishCoupon() {
        // given
        Coupon coupon = Coupon.create("쿠폰명1", 0.1, 1, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);
        couponRepository.updateAvailableCoupon(coupon.getId(), true);

        CouponRequest.Publish request = CouponRequest.Publish.of(coupon.getId());

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/users/{id}/coupons/publish", user.getId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"));

        await()
            .atMost(30, TimeUnit.SECONDS)
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted(() -> {
                Optional<UserCoupon> result = couponRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());
                assertThat(result).isPresent();

                boolean publishable = couponRepository.findPublishableCouponById(coupon.getId());
                assertThat(publishable).isFalse();
            });
    }
}