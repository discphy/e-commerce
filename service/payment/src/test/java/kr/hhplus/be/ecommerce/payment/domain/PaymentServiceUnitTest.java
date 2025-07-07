package kr.hhplus.be.ecommerce.payment.domain;

import kr.hhplus.be.ecommerce.payment.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PaymentServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @DisplayName("결제 시, 잔액 사용에 실패하면 예외가 발생한다.")
    @Test
    void payPaymentWithFailedUseBalance() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1L, 1_000L);

        doThrow(new IllegalArgumentException("잔액 사용에 실패했습니다."))
            .when(paymentClient).useBalance(anyLong(), anyLong());

        // when & then
        assertThatThrownBy(() -> paymentService.payPayment(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액 사용에 실패했습니다.");
        verify(paymentEventPublisher).payFailed(any(PaymentEvent.PayFailed.class));
    }

    @DisplayName("결제 시, 쿠폰 사용에 실패하면 예외가 발생한다.")
    @Test
    void payPaymentWithFailedUseCoupon() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1L, 1_000L);

        doThrow(new IllegalArgumentException("쿠폰 사용에 실패했습니다."))
            .when(paymentClient).useCoupon(anyLong());

        // when & then
        assertThatThrownBy(() -> paymentService.payPayment(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 사용에 실패했습니다.");
        verify(paymentEventPublisher).payFailed(any(PaymentEvent.PayFailed.class));
    }

    @DisplayName("결제 시, 쿠폰이 없으면 쿠폰 사용을 시도하지 않는다.")
    @Test
    void payPaymentWithoutCoupon() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, null, 1_000L);

        // when
        paymentService.payPayment(command);

        // then
        verify(paymentClient).useBalance(anyLong(), anyLong());
        verify(paymentClient, never()).useCoupon(anyLong());
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventPublisher).paid(any(PaymentEvent.Paid.class));
    }

    @DisplayName("결제에 성공하면 결제 이벤트가 발행된다.")
    @Test
    void payPayment() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1L, 1_000L);

        // when
        paymentService.payPayment(command);

        // then
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventPublisher).paid(any(PaymentEvent.Paid.class));
    }

    @DisplayName("결제 취소 시, 결제가 존재해야 한다.")
    @Test
    void cancelPaymentWithoutPayment() {
        // given
        when(paymentRepository.findById(anyLong()))
            .thenThrow(new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("결제 정보를 찾을 수 없습니다.");
    }

    @DisplayName("결제 취소 시, 주문이 존재해야 한다.")
    @Test
    void cancelPaymentWithoutOrder() {
        // given
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById(1L)).thenReturn(payment);

        when(paymentClient.getOrder(anyLong()))
            .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("결제 취소 시, 잔액 환불에 실패하면 예외가 발생한다.")
    @Test
    void cancelPaymentWithFailedRefundBalance() {
        // given
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById(1L)).thenReturn(payment);

        PaymentInfo.Order order = mock(PaymentInfo.Order.class);
        when(paymentClient.getOrder(anyLong())).thenReturn(order);

        doThrow(new IllegalArgumentException("잔액 환불에 실패했습니다."))
            .when(paymentClient).refundBalance(anyLong(), anyLong());

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액 환불에 실패했습니다.");
    }

    @DisplayName("결제 취소 시, 쿠폰 취소에 실패하면 예외가 발생한다.")
    @Test
    void cancelPaymentWithFailedCancelCoupon() {
        // given
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById(1L)).thenReturn(payment);

        PaymentInfo.Order order = mock(PaymentInfo.Order.class);
        when(paymentClient.getOrder(anyLong())).thenReturn(order);
        when(order.getUserCouponId()).thenReturn(1L);

        doThrow(new IllegalArgumentException("쿠폰 취소에 실패했습니다."))
            .when(paymentClient).cancelCoupon(anyLong());

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 취소에 실패했습니다.");
    }

    @DisplayName("결제 취소 시, 쿠폰이 없으면 쿠폰 취소를 시도하지 않는다.")
    @Test
    void cancelPaymentWithoutCoupon() {
        // given
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById(1L)).thenReturn(payment);

        PaymentInfo.Order order = mock(PaymentInfo.Order.class);
        when(paymentClient.getOrder(anyLong())).thenReturn(order);
        when(order.getUserCouponId()).thenReturn(null);

        // when
        paymentService.cancelPayment(1L);

        // then
        verify(paymentClient, never()).cancelCoupon(anyLong());
    }

    @DisplayName("결제를 취소한다.")
    @Test
    void cancelPayment() {
        // given
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById(1L)).thenReturn(payment);

        PaymentInfo.Order order = mock(PaymentInfo.Order.class);
        when(paymentClient.getOrder(anyLong())).thenReturn(order);
        when(order.getUserCouponId()).thenReturn(1L);

        // when
        paymentService.cancelPayment(1L);

        // then
        verify(paymentClient).refundBalance(anyLong(), anyLong());
        verify(paymentClient).cancelCoupon(anyLong());
        verify(payment).cancel();
        verify(paymentEventPublisher).canceled(any(PaymentEvent.Canceled.class));
    }
}