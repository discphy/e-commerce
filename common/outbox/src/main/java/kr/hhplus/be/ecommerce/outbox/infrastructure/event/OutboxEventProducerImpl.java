package kr.hhplus.be.ecommerce.outbox.infrastructure.event;

import kr.hhplus.be.ecommerce.message.MessageProducer;
import kr.hhplus.be.ecommerce.outbox.domain.Outbox;
import kr.hhplus.be.ecommerce.outbox.domain.OutboxEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventProducerImpl implements OutboxEventProducer {

    private final MessageProducer messageProducer;

    @Override
    public void produceEvent(Outbox outbox) {
        messageProducer.send(outbox);
    }
}
