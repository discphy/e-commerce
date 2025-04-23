package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
        Payment payment = paymentRepository.findById(result.getPaymentId()).orElseThrow();
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
}