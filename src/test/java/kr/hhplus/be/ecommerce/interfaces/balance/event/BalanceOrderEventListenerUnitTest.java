package kr.hhplus.be.ecommerce.interfaces.balance.event;

import kr.hhplus.be.ecommerce.domain.balance.BalanceEvent;
import kr.hhplus.be.ecommerce.domain.balance.BalanceEventPublisher;
import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderProcesses;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BalanceOrderEventListenerUnitTest extends MockTestSupport {

    @InjectMocks
    private BalanceOrderEventListener eventListener;

    @Mock
    private BalanceService balanceService;

    @Mock
    private BalanceEventPublisher eventPublisher;

    @DisplayName("주문 생성 시, 잔액 사용 이벤트를 발행한다.")
    @Test
    void handleCreated() {
        // given
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        // when
        eventListener.handle(event);

        // then
        verify(eventPublisher, times(1)).used(any(BalanceEvent.Used.class));
    }

    @DisplayName("주문 생성 시, 잔액 사용 실패 이벤트를 발행한다.")
    @Test
    void handleCreatedWithFailed() {
        // given
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        doThrow(new RuntimeException("잔고 충전 실패"))
            .when(balanceService).useBalance(any());

        // when
        eventListener.handle(event);

        // then
        verify(eventPublisher, times(1)).useFailed(any(BalanceEvent.UseFailed.class));
    }

    @DisplayName("주문 실패 시, 잔액 환불한다.")
    @Test
    void handleFailed() {
        // given
        OrderEvent.Failed event = mock(OrderEvent.Failed.class);
        OrderProcesses processes = mock(OrderProcesses.class);

        when(event.getProcesses())
            .thenReturn(processes);

        when(processes.isSuccess(any()))
            .thenReturn(true);

        // when
        eventListener.handle(event);

        // then
        verify(balanceService, times(1)).refundBalance(any());
    }

}