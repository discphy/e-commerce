package kr.hhplus.be.ecommerce.balance.interfaces.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import kr.hhplus.be.ecommerce.balance.domain.Balance;
import kr.hhplus.be.ecommerce.balance.domain.BalanceClient;
import kr.hhplus.be.ecommerce.balance.domain.BalanceInfo;
import kr.hhplus.be.ecommerce.balance.domain.BalanceRepository;
import kr.hhplus.be.ecommerce.balance.support.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.balance.support.database.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


class BalanceControllerE2ETest extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private BalanceRepository balanceRepository;

    @MockitoBean
    private BalanceClient balanceClient;

    private BalanceInfo.User user;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.clean();

        user = BalanceInfo.User.of(1L, "항플");

        Mockito.when(balanceClient.getUser(user.getUserId()))
            .thenReturn(user);
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() {
        // given
        Balance balance = Balance.create(user.getUserId());
        balance.charge(100_000L);
        balanceRepository.save(balance);

        // when & then
        given()
        .when()
            .get("/api/v1/users/{id}/balance", user.getUserId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"))
            .body("data.amount", equalTo(100_000));
    }

    @DisplayName("잔액이 없으면 조회 시 0원을 반환한다.")
    @Test
    void getBalanceWithoutBalance() {
        // when & then
        given()
            .when()
            .get("/api/v1/users/{id}/balance", user.getUserId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"))
            .body("data.amount", equalTo(0));
    }

    @DisplayName("잔액 충전 시, 최대 금액을 초과할 수 없다.")
    @Test
    void chargeBalanceWithOverMaxAmount() {
        // given
        Balance balance = Balance.create(user.getUserId());
        balance.charge(10_000_000L);
        balanceRepository.save(balance);

        BalanceRequest.Charge request = BalanceRequest.Charge.of(1L);

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/users/{id}/balance/charge", user.getUserId())
        .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", equalTo(400))
            .body("message", equalTo("최대 금액을 초과할 수 없습니다."));
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargeBalance() {
        // given
        Balance balance = Balance.create(user.getUserId());
        balance.charge(10_000L);
        balanceRepository.save(balance);

        BalanceRequest.Charge request = BalanceRequest.Charge.of(1_000_000L);

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/users/{id}/balance/charge", user.getUserId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"));
    }
}
