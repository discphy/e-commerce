package kr.hhplus.be.ecommerce.outbox.infrastructure.repository;

import kr.hhplus.be.ecommerce.outbox.domain.Outbox;
import kr.hhplus.be.ecommerce.outbox.domain.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public List<Outbox> findPendingEvent(LocalDateTime createdAt, Pageable pageable) {
        return outboxJpaRepository.findAllByCreatedAtLessThanEqualOrderByCreatedAtAsc(createdAt, pageable);
    }

    @Override
    public Optional<Outbox> findById(Long id) {
        return outboxJpaRepository.findById(id);
    }

    @Override
    public void deleteByEventId(String eventId) {
        outboxJpaRepository.deleteByEventId(eventId);
    }
}
