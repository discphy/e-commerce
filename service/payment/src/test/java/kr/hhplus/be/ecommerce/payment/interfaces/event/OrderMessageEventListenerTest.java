package kr.hhplus.be.ecommerce.payment.interfaces.event;

import kr.hhplus.be.ecommerce.payment.domain.PaymentService;
import kr.hhplus.be.ecommerce.payment.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private OrderMessageEventListener paymentOrderMessageEventListener;

    @Mock
    private PaymentService paymentService;

    @DisplayName("주문 생성 이벤트를 수신하면 결제 서비스에 결제 요청을 한다.")
    @Test
    void handleOrderCreated() {
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
        paymentOrderMessageEventListener.handleOrderCreated(message, ack);

        // then
        verify(paymentService).payPayment(any());
        verify(ack).acknowledge();
    }

    @DisplayName("주문 완료 실패 이벤트를 수신하면 결제 서비스에 결제 취소 요청을 한다.")
    @Test
    void handleOrderCompleteFailed() {
        // given
        String message = """
            {
            	"eventId": "fee5d5ce-cdf7-4797-8baa-0cad19f80153",
            	"eventType": "ORDER_COMPLETE_FAILED",
            	"payload": {
            		"orderId": 1
            	}
            }
            """;
        Acknowledgment ack = mock(Acknowledgment.class);

        // when
        paymentOrderMessageEventListener.handleOrderCompleteFailed(message, ack);

        // then
        verify(paymentService).cancelPayment(1L);
        verify(ack).acknowledge();
    }

}