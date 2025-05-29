package kr.hhplus.be.ecommerce.infrastructure.outbox.event;

import kr.hhplus.be.ecommerce.domain.outbox.Outbox;
import kr.hhplus.be.ecommerce.domain.outbox.OutboxEvent;
import kr.hhplus.be.ecommerce.support.event.Event;
import kr.hhplus.be.ecommerce.support.event.EventType;
import kr.hhplus.be.ecommerce.support.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisherImpl implements OutboxEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public <T> void publishEvent(EventType type, Long partitionKey, T payload) {
        Outbox outbox = create(type, partitionKey, payload);
        OutboxEvent.Auto event = OutboxEvent.Auto.of(outbox);

        eventPublisher.publishEvent(event);
    }

    @Override
    public <T> void publishManualEvent(EventType type, Long partitionKey, T payload) {
        Outbox outbox = create(type, partitionKey, payload);
        OutboxEvent.Manual event = OutboxEvent.Manual.of(outbox);

        eventPublisher.publishEvent(event);
    }

    private <T> Outbox create(EventType type, Long partitionKey, T payload) {
        String eventId = UUID.randomUUID().toString();
        return Outbox.create(
            eventId,
            type,
            partitionKey,
            Event.of(eventId, type, payload).toJson()
        );
    }
}
