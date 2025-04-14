package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class PaymentRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("결제를 저장한다.")
    @Test
    void save() {
        // given
        Payment payment = Payment.create(1L, 100_000L);

        // when
        Payment result = paymentRepository.save(payment);

        // then
        assertThat(result).isEqualTo(payment);
    }

    @DisplayName("기간 내에 결제가 완료된 결제 내역을 조회한다.")
    @Test
    void findCompletedPaymentsWithIn() {
        // given
        LocalDateTime startDateTime = LocalDateTime.of(2025, 4, 14, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 4, 15, 0, 0, 0);

        Payment payment1 = Payment.builder()
            .orderId(1L)
            .amount(100_000L)
            .paidAt(LocalDateTime.of(2025, 4, 14, 12, 0, 0))
            .paymentStatus(PaymentStatus.COMPLETED)
            .build();
        Payment payment2 = Payment.builder()
            .orderId(2L)
            .amount(200_000L)
            .paidAt(LocalDateTime.of(2025, 4, 14, 13, 0, 0))
            .paymentStatus(PaymentStatus.COMPLETED)
            .build();
        Payment payment3 = Payment.builder()
            .orderId(3L)
            .amount(300_000L)
            .paidAt(LocalDateTime.of(2025, 4, 16, 14, 0, 0))
            .paymentStatus(PaymentStatus.COMPLETED)
            .build();
        Payment payment4 = Payment.builder()
            .orderId(4L)
            .amount(400_000L)
            .paidAt(LocalDateTime.of(2025, 4, 14, 15, 0, 0))
            .paymentStatus(PaymentStatus.CANCELLED)
            .build();

        List.of(payment1, payment2, payment3, payment4).forEach(paymentRepository::save);

        // when
        List<Payment> result = paymentRepository.findCompletedPaymentsWithIn(
            List.of(PaymentStatus.COMPLETED),
            startDateTime,
            endDateTime
        );

        // then
        assertThat(result).hasSize(2)
            .extracting(Payment::getOrderId)
            .containsExactlyInAnyOrder(1L, 2L);
    }

}