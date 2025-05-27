package kr.hhplus.be.ecommerce.interfaces.order.event;

import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.stock.StockEvent;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class OrderStockEventListenerUnitTest extends MockTestSupport {

    @InjectMocks
    private OrderStockEventListener eventListener;

    @Mock
    private OrderService orderService;

    @DisplayName("재고 차감 성공 시, 주문 프로세스를 성공 갱신한다.")
    @Test
    void handleDeducted() {
        // given
        StockEvent.Deducted event = mock(StockEvent.Deducted.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).updateProcess(any());
    }

    @DisplayName("재고 차감 실패 시, 주문 프로세스를 실패 갱신한다.")
    @Test
    void handleDeductFailed() {
        // given
        StockEvent.DeductFailed event = mock(StockEvent.DeductFailed.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).updateProcess(any());
    }
}