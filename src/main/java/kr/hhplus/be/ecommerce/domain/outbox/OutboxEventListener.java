package kr.hhplus.be.ecommerce.domain.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final OutboxService outboxService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent.Auto event) {
        log.info("createOutbox - Auto 아웃 박스 이벤트 수신: {}", event.getOutbox().getTopic());
         outboxService.createOutbox(event.getOutbox());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produceEvent(OutboxEvent.Auto event) {
        log.info("produceEvent - Auto 아웃 박스 이벤트 수신: {}", event.getOutbox().getTopic());
        outboxService.produceEvent(event.getOutbox());
    }

    @Async
    @EventListener
    public void handle(OutboxEvent.Manual event) {
        log.info("produceEvent - Manual 아웃 박스 이벤트 수신: {}", event.getOutbox().getTopic());
        outboxService.createOutbox(event.getOutbox());
        outboxService.produceEvent(event.getOutbox());
    }
}
