package kr.hhplus.be.ecommerce.outbox.domain;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    List<Outbox> findPendingEvent(LocalDateTime createdAt, Pageable pageable);

    Optional<Outbox> findById(Long id);

    void deleteByEventId(String eventId);
}
