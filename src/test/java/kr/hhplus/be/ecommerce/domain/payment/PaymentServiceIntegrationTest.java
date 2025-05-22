package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class PaymentServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("주문을 결제 한다.")
    @Test
    void pay() {
        // given
        PaymentCommand.Payment command =  PaymentCommand.Payment.of(1L, 1L, 100_000L);

        // when
        PaymentInfo.Payment result = paymentService.pay(command);

        // then
        Payment payment = paymentRepository.findById(result.getPaymentId());
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @DisplayName("결제 취소 시, 결제가 존재해야 한다.")
    @Test
    void cancelPaymentWithoutPayment() {
        // when & then
        assertThatThrownBy(() -> paymentService.cancelPayment(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("결제 정보를 찾을 수 없습니다.");
    }

    @DisplayName("결제 취소를 한다.")
    @Test
    void cancelPayment() {
        // given
        PaymentCommand.Payment command =  PaymentCommand.Payment.of(1L, 1L, 100_000L);
        PaymentInfo.Payment result = paymentService.pay(command);

        // when
        paymentService.cancelPayment(result.getPaymentId());

        // then
        Payment payment = paymentRepository.findById(result.getPaymentId());
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
    }
}