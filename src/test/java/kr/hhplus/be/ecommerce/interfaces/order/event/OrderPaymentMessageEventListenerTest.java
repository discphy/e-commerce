package kr.hhplus.be.ecommerce.interfaces.order.event;

import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderPaymentMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private OrderPaymentMessageEventListener orderPaymentMessageEventListener;

    @Mock
    private OrderService orderService;

    @DisplayName("결제 완료 이벤트를 수신하면 주문을 완료한다.")
    @Test
    void handlePaymentPaid() {
        // given
        String message = """
            {
            	"eventId": "9b340cc0-6fc6-4f0e-b334-fa183ed4ff3a",
            	"eventType": "PAYMENT_PAID",
            	"payload": {
            		"paymentId": 1,
            		"orderId": 1,
            		"userId": 1,
            		"totalPrice": 2000000
            	}
            }
            """;
        Acknowledgment ack = mock(Acknowledgment.class);

        // when
        orderPaymentMessageEventListener.handlePaymentPaid(message, ack);

        // then
        verify(orderService).completedOrder(eq(1L));
        verify(ack).acknowledge();
    }

    @DisplayName("결제 실패 이벤트를 수신하면 주문을 취소한다.")
    @Test
    void handlePaymentFailed() {
        // given
        String message = """
            {
            	"eventId": "9b340cc0-6fc6-4f0e-b334-fa183ed4ff3a",
            	"eventType": "PAYMENT_FAILED",
            	"payload": {
            		"orderId": 1
            	}
            }
            """;
        Acknowledgment ack = mock(Acknowledgment.class);

        // when
        orderPaymentMessageEventListener.handlePaymentFailed(message, ack);

        // then
        verify(orderService).cancelOrder(eq(1L));
        verify(ack).acknowledge();
    }

    @DisplayName("결제 취소 이벤트를 수신하면 주문을 취소한다.")
    @Test
    void handlePaymentCanceled() {
        // given
        String message = """
            {
            	"eventId": "9b340cc0-6fc6-4f0e-b334-fa183ed4ff3a",
            	"eventType": "PAYMENT_CANCELED",
            	"payload": {
            		"orderId": 1
            	}
            }
            """;
        Acknowledgment ack = mock(Acknowledgment.class);

        // when
        orderPaymentMessageEventListener.handlePaymentCanceled(message, ack);

        // then
        verify(orderService).cancelOrder(eq(1L));
        verify(ack).acknowledge();
    }


}