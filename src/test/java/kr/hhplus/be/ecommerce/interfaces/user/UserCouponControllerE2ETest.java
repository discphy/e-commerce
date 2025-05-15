package kr.hhplus.be.ecommerce.interfaces.user;

import io.restassured.http.ContentType;
import kr.hhplus.be.ecommerce.domain.coupon.Coupon;
import kr.hhplus.be.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.be.ecommerce.domain.coupon.CouponStatus;
import kr.hhplus.be.ecommerce.domain.user.User;
import kr.hhplus.be.ecommerce.domain.user.UserCoupon;
import kr.hhplus.be.ecommerce.domain.user.UserCouponRepository;
import kr.hhplus.be.ecommerce.domain.user.UserRepository;
import kr.hhplus.be.ecommerce.support.E2EControllerTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class UserCouponControllerE2ETest extends E2EControllerTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);
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
        userCouponRepository.save(userCoupon1);
        userCouponRepository.save(userCoupon2);

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
        Coupon coupon = Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCouponRequest.Publish request = UserCouponRequest.Publish.of(coupon.getId());

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
    }
}