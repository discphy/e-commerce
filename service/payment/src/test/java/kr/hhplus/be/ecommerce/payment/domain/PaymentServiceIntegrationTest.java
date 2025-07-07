package kr.hhplus.be.ecommerce.payment.domain;

import kr.hhplus.be.ecommerce.outbox.domain.OutboxEvent;
import kr.hhplus.be.ecommerce.payment.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Transactional
class PaymentServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private PaymentClient paymentClient;

    @MockitoSpyBean
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
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
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
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }

    @DisplayName("결제 시, 쿠폰이 없으면 쿠폰 사용을 시도하지 않는다.")
    @Test
    void payPaymentWithoutCoupon() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, null, 1_000L);

        // when
        paymentService.payPayment(command);

        // then
        verify(paymentClient, never()).useCoupon(anyLong());
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }

    @DisplayName("결제에 성공하면 결제 이벤트가 발행된다.")
    @Test
    void payPayment() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1L, 1_000L);

        // when
        paymentService.payPayment(command);

        // then
        verify(paymentEventPublisher).paid(any(PaymentEvent.Paid.class));
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }

    @DisplayName("결제 취소 시, 결제가 존재해야 한다.")
    @Test
    void cancelPaymentWithoutPayment() {
        // when & then
        Long paymentId = -1L;

        assertThatThrownBy(() -> paymentService.cancelPayment(paymentId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("결제 정보를 찾을 수 없습니다.");
    }

    @DisplayName("결제 취소 시, 주문이 존재해야 한다.")
    @Test
    void cancelPaymentWithoutOrder() {
        // given
        Payment payment = Payment.create(1L, 1_000L);
        paymentRepository.save(payment);

        doThrow(new IllegalArgumentException("주문이 존재하지 않습니다."))
            .when(paymentClient).getOrder(anyLong());

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(payment.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("결제 취소 시, 잔액 환불에 실패하면 예외가 발생한다.")
    @Test
    void cancelPaymentWithFailedRefundBalance() {
        // given
        Payment payment = Payment.create(1L, 1_000L);
        paymentRepository.save(payment);

        PaymentInfo.Order order = PaymentInfo.Order.of(payment.getOrderId(), 1L, 1L, payment.getAmount());

        doReturn(order).when(paymentClient).getOrder(anyLong());
        doThrow(new IllegalArgumentException("잔액 환불에 실패했습니다."))
            .when(paymentClient).refundBalance(anyLong(), anyLong());

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(payment.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액 환불에 실패했습니다.");
    }

    @DisplayName("결제 취소 시, 쿠폰 취소에 실패하면 예외가 발생한다.")
    @Test
    void cancelPaymentWithFailedCancelCoupon() {
        // given
        Payment payment = Payment.create(1L, 1_000L);
        paymentRepository.save(payment);

        PaymentInfo.Order order = PaymentInfo.Order.of(payment.getOrderId(), 1L, 1L, payment.getAmount());

        doReturn(order).when(paymentClient).getOrder(anyLong());
        doThrow(new IllegalArgumentException("쿠폰 환불에 실패했습니다."))
            .when(paymentClient).cancelCoupon(anyLong());

        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(payment.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰 환불에 실패했습니다.");
    }

    @DisplayName("결제 취소 시, 쿠폰이 없으면 쿠폰 취소를 시도하지 않는다.")
    @Test
    void cancelPaymentWithoutCoupon() {
        // given
        Payment payment = Payment.create(1L, 1_000L);
        paymentRepository.save(payment);

        PaymentInfo.Order order = PaymentInfo.Order.of(payment.getOrderId(), 1L, null, payment.getAmount());

        doReturn(order).when(paymentClient).getOrder(anyLong());

        // when
        paymentService.cancelPayment(payment.getId());

        // then
        verify(paymentClient, never()).cancelCoupon(anyLong());
    }

    @DisplayName("결제를 취소한다.")
    @Test
    void cancelPayment() {
        // given
        Payment payment = Payment.create(1L, 1_000L);
        paymentRepository.save(payment);

        PaymentInfo.Order order = PaymentInfo.Order.of(payment.getOrderId(), 1L, 1L, payment.getAmount());

        doReturn(order).when(paymentClient).getOrder(anyLong());

        // when
        paymentService.cancelPayment(payment.getId());

        // then
        Payment result = paymentRepository.findById(payment.getId());
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
        verify(paymentEventPublisher).canceled(any(PaymentEvent.Canceled.class));
        assertThat(events.stream(OutboxEvent.class).count()).isEqualTo(1);
    }
}