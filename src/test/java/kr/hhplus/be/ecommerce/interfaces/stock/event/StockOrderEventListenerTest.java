package kr.hhplus.be.ecommerce.interfaces.stock.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderProcesses;
import kr.hhplus.be.ecommerce.domain.stock.StockEvent;
import kr.hhplus.be.ecommerce.domain.stock.StockEventPublisher;
import kr.hhplus.be.ecommerce.domain.stock.StockService;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockOrderEventListenerTest extends MockTestSupport {

    @InjectMocks
    private StockOrderEventListener eventListener;

    @Mock
    private StockService stockService;

    @Mock
    private StockEventPublisher eventPublisher;

    @DisplayName("주문 생성 시, 재고 차감 이벤트를 발행한다.")
    @Test
    void handleCreated() {
        // given
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        // when
        eventListener.handle(event);

        // then
        verify(stockService, times(1)).deductStock(any());
        verify(eventPublisher, times(1)).deducted(any(StockEvent.Deducted.class));
    }

    @DisplayName("주문 생성 시, 재고 차감 실패 이벤트를 발행한다.")
    @Test
    void handleCreatedWithFailed() {
        // given
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        doThrow(new RuntimeException("재고 차감 실패"))
            .when(stockService).deductStock(any());

        // when
        eventListener.handle(event);

        // then
        verify(eventPublisher, times(1)).deductFailed(any(StockEvent.DeductFailed.class));
    }

    @DisplayName("주문 실패 시, 재고 복구한다.")
    @Test
    void handleFailed() {
        // given
        OrderEvent.Failed event = mock(OrderEvent.Failed.class);
        OrderProcesses processes = mock(OrderProcesses.class);

        when(event.getProcesses())
            .thenReturn(processes);

        when(event.getProcesses().isSuccess(any()))
            .thenReturn(true);

        // when
        eventListener.handle(event);

        // then
        verify(stockService, times(1)).restoreStock(any());
    }

}