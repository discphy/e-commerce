package kr.hhplus.be.ecommerce.support;

import kr.hhplus.be.ecommerce.support.database.RedisCacheCleaner;
import kr.hhplus.be.ecommerce.support.database.RedisKeyCleaner;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport extends ContainerTestSupport {

    @Autowired
    private RedisKeyCleaner redisKeyCleaner;

    @Autowired
    private RedisCacheCleaner redisCacheCleaner;

    @AfterEach
    void tearDown() {
        redisKeyCleaner.clean();
        redisCacheCleaner.clean();
    }
}
