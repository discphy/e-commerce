package kr.hhplus.be.ecommerce.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @DisplayName("결제 금액은 0보다 커야 한다.")
    @Test
    void createWithInvalidAmount() {
        // when & then
        assertThatThrownBy(() -> Payment.create(-1L, 0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("결제 금액은 0보다 커야 합니다.");
    }

    @DisplayName("결제 가능 상태에서 결제가 가능하다.")
    @ParameterizedTest
    @ValueSource(strings = {
        "COMPLETED",
        "FAILED",
        "CANCELED",
    })
    void payWithNonPayableStatus(PaymentStatus status) {
        // given
        Payment payment = Payment.builder()
            .paymentStatus(status)
            .build();

        // when & then
        assertThatThrownBy(payment::pay)
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("결제 가능 상태가 아닙니다.");
    }

    @DisplayName("결제를 한다.")
    @Test
    void pay() {
        // given
        Payment payment = Payment.create(1L, 100_000L);

        // when
        payment.pay();

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @DisplayName("결제 취소를 한다.")
    @Test
    void cancel() {
        // given
        Payment payment = Payment.create(1L, 100_000L);

        // when
        payment.cancel();

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
    }

}