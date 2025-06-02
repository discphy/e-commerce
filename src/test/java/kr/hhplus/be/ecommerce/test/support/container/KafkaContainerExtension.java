package kr.hhplus.be.ecommerce.test.support.container;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainerExtension implements BeforeAllCallback {

    private static final KafkaContainer KAFKA_CONTAINER;

    static {
        KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
        );
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        if (KAFKA_CONTAINER.isRunning()) {
            return;
        }

        KAFKA_CONTAINER.start();
    }

    public static KafkaContainer getContainer() {
        return KAFKA_CONTAINER;
    }
}
