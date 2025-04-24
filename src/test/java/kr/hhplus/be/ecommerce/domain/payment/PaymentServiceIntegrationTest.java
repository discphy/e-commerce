package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    @DisplayName("최근 3일간 결제된 주문을 조회한다.")
    @Test
    void getCompletedOrdersBetweenDays() {
        // given
        int recentDays = 3;
        LocalDateTime now = LocalDateTime.now();
        Payment payment1 = Payment.builder()
            .orderId(1L)
            .amount(100_000L)
            .paidAt(now.minusDays(1))
            .paymentStatus(PaymentStatus.COMPLETED)
            .build();
        Payment payment2 = Payment.builder()
            .orderId(2L)
            .amount(200_000L)
            .paidAt(now.minusDays(2))
            .paymentStatus(PaymentStatus.COMPLETED)
            .build();
        Payment excludePaidAtPayment = Payment.builder()
            .orderId(3L)
            .amount(300_000L)
            .paidAt(now.plusDays(1))
            .paymentStatus(PaymentStatus.COMPLETED)
            .build();
        Payment excludeStatusPayment = Payment.builder()
            .orderId(4L)
            .amount(400_000L)
            .paidAt(now.minusDays(2))
            .paymentStatus(PaymentStatus.CANCELLED)
            .build();

        List.of(payment1, payment2, excludePaidAtPayment, excludeStatusPayment).forEach(paymentRepository::save);

        // when
        PaymentInfo.Orders result = paymentService.getCompletedOrdersBetweenDays(recentDays);

        // then
        assertThat(result.getOrderIds())
            .containsExactlyInAnyOrder(1L, 2L);
    }

}