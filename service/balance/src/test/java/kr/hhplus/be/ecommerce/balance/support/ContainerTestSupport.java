package kr.hhplus.be.ecommerce.balance.support;

import kr.hhplus.be.ecommerce.balance.support.container.MySQLContainerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@ExtendWith({
    MySQLContainerExtension.class,
})
public abstract class ContainerTestSupport {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL
        MySQLContainer<?> mySQLContainer = MySQLContainerExtension.getContainer();
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }
}
