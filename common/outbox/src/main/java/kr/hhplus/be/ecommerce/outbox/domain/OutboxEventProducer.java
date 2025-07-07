package kr.hhplus.be.ecommerce.outbox.domain;

public interface OutboxEventProducer {

    void produceEvent(Outbox outbox);
}
