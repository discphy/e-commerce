package kr.hhplus.be.ecommerce.product.support;

import kr.hhplus.be.ecommerce.product.support.database.DatabaseCleaner;
import kr.hhplus.be.ecommerce.product.support.database.RedisCacheCleaner;
import kr.hhplus.be.ecommerce.product.support.database.RedisKeyCleaner;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@ActiveProfiles("test")
@RecordApplicationEvents
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport extends ContainerTestSupport {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private RedisKeyCleaner redisKeyCleaner;

    @Autowired
    protected RedisCacheCleaner redisCacheCleaner;

    @Autowired
    protected ApplicationEvents events;

    @AfterEach
    void tearDown() {
        databaseCleaner.clean();
        redisKeyCleaner.clean();
        redisCacheCleaner.clean();
    }

}
