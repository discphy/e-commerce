package kr.hhplus.be.ecommerce.product.interfaces.rank.event;

import kr.hhplus.be.ecommerce.product.domain.rank.RankService;
import kr.hhplus.be.ecommerce.product.interfaces.event.OrderMessageEventListener;
import kr.hhplus.be.ecommerce.product.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RankOrderMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private OrderMessageEventListener rankOrderMessageEventListener;

    @Mock
    private RankService rankService;

    @DisplayName("주문 완료 이벤트를 수신하면 랭킹을 생성한다")
    @Test
    void handleOrderCompleted() {
        // given
        String message = """
            {
            	"eventId": "0bf2fdd3-ea70-4e43-b2e7-b614837ddaed",
            	"eventType": "ORDER_COMPLETED",
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
        rankOrderMessageEventListener.handleOrderCompleted(message, ack);

        // then
        verify(rankService).createSellRank(any());
        verify(ack).acknowledge();
    }
}