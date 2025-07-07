package kr.hhplus.be.ecommerce.message;

import kr.hhplus.be.ecommerce.message.event.Event;
import kr.hhplus.be.ecommerce.message.event.EventType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DefaultMessage implements Message {

    private final String topic;
    private final String key;
    private final String payload;

    private DefaultMessage(String topic, String key, String payload) {
        this.topic = topic;
        this.key = key;
        this.payload = payload;
    }

    public static <T> DefaultMessage of(EventType type, Long key, T payload) {
        return new DefaultMessage(
            type.getTopic(),
            String.valueOf(key),
            Event.of(UUID.randomUUID().toString(), type, payload).toJson()
        );
    }
}
