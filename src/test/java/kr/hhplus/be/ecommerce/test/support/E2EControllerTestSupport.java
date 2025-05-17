package kr.hhplus.be.ecommerce.test.support;

import io.restassured.RestAssured;
import kr.hhplus.be.ecommerce.test.support.database.DatabaseCleaner;
import kr.hhplus.be.ecommerce.test.support.database.RedisCacheCleaner;
import kr.hhplus.be.ecommerce.test.support.database.RedisKeyCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

public abstract class E2EControllerTestSupport extends IntegrationTestSupport {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private RedisCacheCleaner redisCacheCleaner;

    @Autowired
    private RedisKeyCleaner redisKeyCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
        redisCacheCleaner.clean();
        redisKeyCleaner.clean();
    }
}
