package kr.hhplus.be.ecommerce.outbox.infrastructure.event;

import kr.hhplus.be.ecommerce.message.event.Event;
import kr.hhplus.be.ecommerce.message.event.EventType;
import kr.hhplus.be.ecommerce.outbox.OutboxEventPublisher;
import kr.hhplus.be.ecommerce.outbox.domain.Outbox;
import kr.hhplus.be.ecommerce.outbox.domain.OutboxEvent;
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
        String eventId = UUID.randomUUID().toString();
        Outbox outbox = Outbox.create(
            eventId,
            type,
            partitionKey,
            Event.of(eventId, type, payload).toJson()
        );

        eventPublisher.publishEvent(OutboxEvent.of(outbox));
    }
}
