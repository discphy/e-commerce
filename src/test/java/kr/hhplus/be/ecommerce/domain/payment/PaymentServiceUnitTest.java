package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
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

    @DisplayName("결제를 생성하고 저장한다.")
    @Test
    void pay() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1_000L);

        // when
        paymentService.pay(command);

        // then
        verify(paymentRepository, times(1)).save(any(Payment.class));
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

    @DisplayName("결제 취소를 한다.")
    @Test
    void cancelPayment() {
        // given
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById(1L)).thenReturn(payment);

        // when
        paymentService.cancelPayment(1L);

        // then
        verify(payment, times(1)).cancel();
    }
}