package kr.hhplus.be.ecommerce.message.event;

import kr.hhplus.be.ecommerce.serialize.DataSerializer;
import lombok.Getter;

import java.util.Optional;

@Getter
public class Event<T> {

    private final String eventId;
    private final EventType eventType;
    private final T payload;

    private Event(String eventId, EventType eventType, T payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
    }

    public static <T> Event<T> of(String eventId, EventType eventType, T payload) {
        return new Event<>(eventId, eventType, payload);
    }

    public static <T> Event<T> of(String json, Class<T> type) {
        EventRaw raw = DataSerializer.deserialize(json, EventRaw.class);

        return Optional.ofNullable(raw)
            .map(r -> raw.toEvent(type))
            .orElse(null);
    }

    public String toJson() {
        return DataSerializer.serialize(this);
    }

    @Getter
    private static class EventRaw {

        private String eventId;
        private String eventType;
        private Object payload;

        public <T> Event<T> toEvent(Class<T> type) {
            return Event.of(
                eventId,
                EventType.valueOf(eventType),
                DataSerializer.deserialize(payload, type)
            );
        }
    }
}
