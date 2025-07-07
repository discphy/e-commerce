package kr.hhplus.be.ecommerce.message.event;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @DisplayName("이벤트 객체를 생성한다.")
    @Test
    void of() {
        // given
        String eventId = "event-id";
        EventType eventType = EventType.ORDER_COMPLETED;
        TestPayload payload = new TestPayload("test data");

        // when
        Event<TestPayload> event = Event.of(eventId, eventType, payload);

        // then
        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getPayload()).isEqualTo(payload);
    }

    @DisplayName("JSON 문자열로부터 이벤트 객체를 생성한다.")
    @Test
    void ofFromJson() {
        // given
        String eventId = "event-id";
        EventType eventType = EventType.ORDER_COMPLETED;
        TestPayload payload = new TestPayload("test data");
        String json = Event.of(eventId, eventType, payload).toJson();

        // when
        Event<TestPayload> event = Event.of(json, TestPayload.class);

        // then
        TestPayload resultPayload = event.getPayload();
        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(resultPayload.data).isEqualTo("test data");
    }

    @DisplayName("이벤트 객체를 JSON 문자열로 변환한다.")
    @Test
    void toJson() {
        // given
        String eventId = "event-id";
        EventType eventType = EventType.ORDER_COMPLETED;
        TestPayload payload = new TestPayload("test data");
        Event<TestPayload> event = Event.of(eventId, eventType, payload);

        // when
        String json = event.toJson();

        // then
        assertThat(json).isNotNull();
        assertThat(json).contains(eventId, eventType.name(), "test data");
    }

    @NoArgsConstructor
    static class TestPayload {
        private String data;

        public TestPayload(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }
    }

}