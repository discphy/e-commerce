package kr.hhplus.be.ecommerce.domain.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final OutboxEventProducer outboxEventProducer;

    @Transactional
    public Outbox createOutbox(Outbox outbox) {
        return outboxRepository.save(outbox);
    }

    public void produceEvent(Outbox outbox) {
        outboxEventProducer.produceEvent(outbox);
    }

    @Transactional
    public void publishPendingEvent() {
        outboxRepository.findPendingEvent(
            LocalDateTime.now().minusSeconds(10),
            Pageable.ofSize(100)
        ).forEach(this::produceEvent);
    }

    @Transactional
    public void clearOutbox(String eventId) {
        outboxRepository.deleteByEventId(eventId);
    }
}
