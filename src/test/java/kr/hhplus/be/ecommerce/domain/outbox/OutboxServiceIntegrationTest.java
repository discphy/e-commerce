package kr.hhplus.be.ecommerce.domain.outbox;

import kr.hhplus.be.ecommerce.support.event.EventType;
import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class OutboxServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private OutboxRepository outboxRepository;

    @DisplayName("아웃박스를 생성 한다.")
    @Test
    void createOutbox() {
        // given
        Outbox outbox = Outbox.create("eventId", EventType.ORDER_COMPLETED, 1L, "{\"orderId\": 1}");

        // when
        Outbox result = outboxService.createOutbox(outbox);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEventType()).isEqualTo(outbox.getEventType());
        assertThat(result.getPartitionKey()).isEqualTo(outbox.getPartitionKey());
        assertThat(result.getPayload()).isEqualTo(outbox.getPayload());
    }

    @DisplayName("아웃박스 이벤트를 삭제한다.")
    @Test
    void clearOutbox() {
        // given
        Outbox outbox = Outbox.create("eventId", EventType.ORDER_COMPLETED, 1L, "{\"orderId\": 1}");
        outboxRepository.save(outbox);

        // when
        outboxService.clearOutbox(outbox.getEventId());

        // then
        Outbox deletedOutbox = outboxRepository.findById(outbox.getId()).orElse(null);
        assertThat(deletedOutbox).isNull();
    }
}