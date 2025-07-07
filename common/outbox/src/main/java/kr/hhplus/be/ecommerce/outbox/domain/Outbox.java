package kr.hhplus.be.ecommerce.outbox.domain;

import jakarta.persistence.*;
import kr.hhplus.be.ecommerce.message.Message;
import kr.hhplus.be.ecommerce.message.event.EventType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox implements Message {

    @Id
    @Column(name = "outbox_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private EventType eventType;

    private Long partitionKey;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime createdAt;

    @Builder
    private Outbox(Long id, String eventId, EventType eventType, Long partitionKey, String payload, LocalDateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.partitionKey = partitionKey;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    public static Outbox create(String eventId, EventType eventType, Long partitionKey, String payload) {
        return Outbox.builder()
            .eventId(eventId)
            .eventType(eventType)
            .partitionKey(partitionKey)
            .payload(payload)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Override
    public String getTopic() {
        return eventType.getTopic();
    }

    @Override
    public String getKey() {
        return String.valueOf(partitionKey);
    }
}
