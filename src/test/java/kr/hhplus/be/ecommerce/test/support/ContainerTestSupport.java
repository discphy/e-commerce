package kr.hhplus.be.ecommerce.test.support;

import kr.hhplus.be.ecommerce.test.support.container.KafkaContainerExtension;
import kr.hhplus.be.ecommerce.test.support.container.MySQLContainerExtension;
import kr.hhplus.be.ecommerce.test.support.container.RedisContainerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;

@ExtendWith({
    MySQLContainerExtension.class,
    RedisContainerExtension.class,
    KafkaContainerExtension.class
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
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);

        // Kafka
        KafkaContainer kafkaContainer = KafkaContainerExtension.getContainer();
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
}
