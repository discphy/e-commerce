package kr.hhplus.be.ecommerce.interfaces.order.event;

import kr.hhplus.be.ecommerce.domain.balance.BalanceEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class OrderBalanceEventListenerUnitTest extends MockTestSupport {

    @InjectMocks
    private OrderBalanceEventListener eventListener;

    @Mock
    private OrderService orderService;

    @DisplayName("잔액 사용 성공 시, 주문 프로세스를 성공 갱신한다.")
    @Test
    void handleUsed() {
        // given
        BalanceEvent.Used event = mock(BalanceEvent.Used.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).updateProcess(any());
    }

    @DisplayName("잔액 사용 실패 시, 주문 프로세스를 실패 갱신한다.")
    @Test
    void handleUseFailed() {
        // given
        BalanceEvent.UseFailed event = mock(BalanceEvent.UseFailed.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).updateProcess(any());
    }
}