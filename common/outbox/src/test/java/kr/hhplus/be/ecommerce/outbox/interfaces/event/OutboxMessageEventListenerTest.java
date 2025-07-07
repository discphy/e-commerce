package kr.hhplus.be.ecommerce.outbox.interfaces.event;

import kr.hhplus.be.ecommerce.outbox.domain.OutboxService;
import kr.hhplus.be.ecommerce.outbox.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OutboxMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private OutboxMessageEventListener outboxMessageEventListener;

    @Mock
    private OutboxService outboxService;

    @DisplayName("아웃박스 이벤트를 수신하고 아웃박스를 비운다.")
    @Test
    void handle() {
        // given
        String message = """
            {
            	"eventId": "fee5d5ce-cdf7-4797-8baa-0cad19f80153",
            	"eventType": "ORDER_CREATED",
            	"payload": {
            		"orderId": 1,
            		"userId": 1,
            		"userCouponId": null,
            		"totalPrice": 2000000,
            		"discountPrice": 0,
            		"orderProducts": [
            			{
            				"orderProductId": 1,
            				"productId": 1,
            				"productName": "Sample Product",
            				"unitPrice": 10000,
            				"quantity": 100
            			},
            			{
            				"orderProductId": 2,
            				"productId": 2,
            				"productName": "Another Product",
            				"unitPrice": 20000,
            				"quantity": 50
            			}
            		]
            	}
            }
            """;
        Acknowledgment ack = mock(Acknowledgment.class);

        // when
        outboxMessageEventListener.handle(message, ack);

        // then
        verify(outboxService).clearOutbox(eq("fee5d5ce-cdf7-4797-8baa-0cad19f80153"));
        verify(ack).acknowledge();
    }

}