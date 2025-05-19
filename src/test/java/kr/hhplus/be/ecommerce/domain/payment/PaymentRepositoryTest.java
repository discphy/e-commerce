package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
}