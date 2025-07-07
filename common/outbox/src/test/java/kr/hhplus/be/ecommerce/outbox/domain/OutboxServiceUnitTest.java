package kr.hhplus.be.ecommerce.outbox.domain;

import kr.hhplus.be.ecommerce.message.event.EventType;
import kr.hhplus.be.ecommerce.outbox.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OutboxServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private OutboxService outboxService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxEventProducer outboxEventProducer;

    @DisplayName("아웃박스를 생성 한다.")
    @Test
    void createOutbox() {
        // given
        Outbox outbox = Outbox.create("eventId", EventType.ORDER_COMPLETED, 1L, "{\"orderId\": 1}");

        when(outboxRepository.save(outbox))
            .thenReturn(Outbox.builder()
                .id(1L)
                .eventType(EventType.ORDER_COMPLETED)
                .partitionKey(1L)
                .payload("{\"orderId\": 1}")
                .createdAt(outbox.getCreatedAt())
                .build()
            );

        // when
        Outbox result = outboxService.createOutbox(outbox);

        // then
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("아웃박스를 발행한다.")
    @Test
    void produceEvent() {
        // given
        Outbox outbox = Outbox.create("eventId", EventType.ORDER_COMPLETED, 1L, "{\"orderId\": 1}");

        // when
        outboxService.produceEvent(outbox);

        // then
        verify(outboxEventProducer).produceEvent(outbox);
    }

    @DisplayName("아웃박스 발행 대기 이벤트를 발행한다.")
    @Test
    void publishPendingEvent() {
        // given
        when(outboxRepository.findPendingEvent(any(LocalDateTime.class), any(Pageable.class)))
            .thenReturn(List.of(
                Outbox.builder()
                    .id(1L)
                    .eventType(EventType.ORDER_COMPLETED)
                    .partitionKey(1L)
                    .payload("{\"orderId\": 1}")
                    .createdAt(LocalDateTime.now())
                    .build(),
                Outbox.builder()
                    .id(2L)
                    .eventType(EventType.ORDER_COMPLETED)
                    .partitionKey(1L)
                    .payload("{\"orderId\": 2}")
                    .createdAt(LocalDateTime.now())
                    .build()
            ));

        // when
        outboxService.publishPendingEvent();

        // then
        verify(outboxEventProducer, times(2)).produceEvent(any(Outbox.class));
    }

    @DisplayName("아웃박스 발행 대기 이벤트가 없으면 발행하지 않는다.")
    @Test
    void publishPendingEventWithNoPendingEvents() {
        // given
        when(outboxRepository.findPendingEvent(any(LocalDateTime.class), any(Pageable.class)))
            .thenReturn(List.of());

        // when
        outboxService.publishPendingEvent();

        // then
        verify(outboxEventProducer, never()).produceEvent(any(Outbox.class));
    }

    @DisplayName("아웃박스를 삭제한다.")
    @Test
    void clearOutbox() {
        // given
        String eventId = "eventId";

        // when
        outboxService.clearOutbox(eventId);

        // then
        verify(outboxRepository).deleteByEventId(eventId);
    }
}