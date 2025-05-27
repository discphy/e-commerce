package kr.hhplus.be.ecommerce.interfaces.order.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderEventPublisher;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.payment.PaymentEvent;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderPaymentEventListenerUnitTest extends MockTestSupport {

    @InjectMocks
    private OrderPaymentEventListener eventListener;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @DisplayName("결제 완료 시 주문 완료 이벤트를 발행한다.")
    @Test
    void handlePaid() {
        // given
        PaymentEvent.Paid event = mock(PaymentEvent.Paid.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderService, times(1)).completedOrder(any());
        verify(orderEventPublisher, times(1)).completed(any(OrderEvent.Completed.class));
    }

    @DisplayName("결제 완료 시, 주문 완료에 실패하면 주문 완료 실패 이벤트를 발행한다.")
    @Test
    void handlePaidFailed() {
        // given
        PaymentEvent.Paid event = mock(PaymentEvent.Paid.class);

        doThrow(new IllegalArgumentException("주문 완료 실패"))
            .when(orderService).completedOrder(any());

        // when
        eventListener.handle(event);

        // then
        verify(orderEventPublisher, times(1)).completeFailed(any(OrderEvent.CompleteFailed.class));
    }

    @DisplayName("결제 취소 시 주문 실패 이벤트를 발행한다.")
    @Test
    void handleCanceled() {
        // given
        PaymentEvent.Canceled event = mock(PaymentEvent.Canceled.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderEventPublisher, times(1)).failed(any(OrderEvent.Failed.class));
    }

    @DisplayName("결제 실패 시 주문 실패 이벤트를 발행한다.")
    @Test
    void handlePayFailed() {
        // given
        PaymentEvent.PayFailed event = mock(PaymentEvent.PayFailed.class);

        // when
        eventListener.handle(event);

        // then
        verify(orderEventPublisher, times(1)).failed(any(OrderEvent.Failed.class));
    }
}