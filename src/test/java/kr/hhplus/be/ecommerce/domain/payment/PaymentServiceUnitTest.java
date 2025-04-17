package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @DisplayName("최근 결제 내역의 주문 ID를 가져온다.")
    @Test
    void getCompletedOrdersWithInDays() {
        // given
        int days = 3;

        List<Payment> payments = List.of(
            Payment.builder()
                .orderId(1L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now().minusDays(1)) // 최근 3일 안에 포함
                .build(),
            Payment.builder()
                .orderId(2L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now().minusDays(2)) // 최근 3일 안에 포함
                .build()
        );

        when(paymentRepository.findCompletedPaymentsWithIn(anyList(), any(), any()))
            .thenReturn(payments);

        // when
        PaymentInfo.Orders orders = paymentService.getCompletedOrdersBetweenDays(days);

        // then
        assertThat(orders.getOrderIds()).hasSize(2)
            .containsExactlyInAnyOrder(1L, 2L);
    }

}