package kr.hhplus.be.ecommerce.interfaces.order.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class OrderEventListenerUnitTest extends MockTestSupport {

    @InjectMocks
    private OrderEventListener eventListener;

    @Mock
    private OrderService orderService;

    @DisplayName("주문 실패 시, 주문을 취소한다.")
    @Test
    void handleFailed() {
        // given
        OrderEvent.Failed event = mock(OrderEvent.Failed.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).cancelOrder(event.getOrderId());
    }

}