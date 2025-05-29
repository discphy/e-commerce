package kr.hhplus.be.ecommerce.infrastructure.message;

import kr.hhplus.be.ecommerce.support.message.Message;
import kr.hhplus.be.ecommerce.support.message.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageProducer implements MessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final int DEFAULT_GET_SYNC_TIMEOUT_SECONDS = 1;

    @Override
    public void send(Message message) {
        log.info("비동기 카프카 메세지 전송 topic={}, payload={}", message.getTopic(), message.getPayload());
        kafkaTemplate.send(message.getTopic(), message.getKey(), message.getPayload());
    }

    @Override
    public void sendSync(Message message) throws Exception {
        log.info("동기 카프카 메세지 전송 topic={}, payload={}", message.getTopic(), message.getPayload());
        kafkaTemplate.send(message.getTopic(), message.getKey(), message.getPayload())
            .get(DEFAULT_GET_SYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
