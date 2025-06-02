package kr.hhplus.be.ecommerce.domain.outbox;

public interface OutboxEventProducer {

    void produceEvent(Outbox outbox);
}
