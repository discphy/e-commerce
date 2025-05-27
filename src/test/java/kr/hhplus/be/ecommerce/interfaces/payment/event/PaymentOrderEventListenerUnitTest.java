package kr.hhplus.be.ecommerce.interfaces.payment.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.payment.PaymentEvent;
import kr.hhplus.be.ecommerce.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.ecommerce.domain.payment.PaymentInfo;
import kr.hhplus.be.ecommerce.domain.payment.PaymentService;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class PaymentOrderEventListenerUnitTest extends MockTestSupport {

    @InjectMocks
    private PaymentOrderEventListener eventListener;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @DisplayName("주문 결제 대기 시, 결제 완료 이벤트를 발행한다.")
    @Test
    void handlePaymentWaited() {
        // given
        OrderEvent.PaymentWaited event = mock(OrderEvent.PaymentWaited.class);
        PaymentInfo.Payment payment = mock(PaymentInfo.Payment.class);

        when(paymentService.pay(any()))
            .thenReturn(payment);

        when(payment.getPaymentId()).thenReturn(1L);

        // when
        eventListener.handle(event);

        // then
        verify(eventPublisher, times(1)).paid(any(PaymentEvent.Paid.class));
    }

    @DisplayName("주문 결제 대기 시, 결제 실패하면 실패 이벤트를 발행한다.")
    @Test
    void handlePaymentWaitedWithFailed() {
        // given
        OrderEvent.PaymentWaited event = mock(OrderEvent.PaymentWaited.class);

        when(paymentService.pay(any()))
            .thenThrow(new RuntimeException("결제 실패"));

        // when
        eventListener.handle(event);

        // then
        verify(eventPublisher, times(1)).payFailed(any(PaymentEvent.PayFailed.class));
    }

    @DisplayName("주문 완료 실패 시, 결제 실패 이벤트를 발행한다.")
    @Test
    void handleCompleteFailed() {
        // given
        OrderEvent.CompleteFailed event = mock(OrderEvent.CompleteFailed.class);

        // when
        eventListener.handle(event);

        // then
        verify(paymentService, times(1)).cancelPayment(event.getPaymentId());
        verify(eventPublisher, times(1)).canceled(any(PaymentEvent.Canceled.class));
    }
}