package kr.hhplus.be.ecommerce.outbox;


import kr.hhplus.be.ecommerce.message.event.EventType;

public interface OutboxEventPublisher {

    <T> void publishEvent(EventType type, Long shardKey, T payload);
}
