package kr.hhplus.be.ecommerce.test.support;

import kr.hhplus.be.ecommerce.test.support.container.MySQLContainerExtension;
import kr.hhplus.be.ecommerce.test.support.container.RedisContainerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

@ExtendWith({
    MySQLContainerExtension.class,
    RedisContainerExtension.class,
})
public abstract class ContainerTestSupport {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL
        MySQLContainer<?> mySQLContainer = MySQLContainerExtension.getContainer();
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        // Redis
        GenericContainer<?> redisContainer = RedisContainerExtension.getContainer();
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
    }
}
