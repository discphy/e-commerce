package kr.hhplus.be.ecommerce.infrastructure.outbox.event;

import kr.hhplus.be.ecommerce.domain.outbox.Outbox;
import kr.hhplus.be.ecommerce.domain.outbox.OutboxEventProducer;
import kr.hhplus.be.ecommerce.support.message.MessageProducer;
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
