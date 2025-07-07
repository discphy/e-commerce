package kr.hhplus.be.ecommerce.order.support.container;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisContainerExtension implements BeforeAllCallback {

    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(6379)
            .withCommand("redis-server", "--appendonly", "yes");
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        if (REDIS_CONTAINER.isRunning()) {
            return;
        }

        REDIS_CONTAINER.start();
    }

    public static GenericContainer<?> getContainer() {
        return REDIS_CONTAINER;
    }
}
