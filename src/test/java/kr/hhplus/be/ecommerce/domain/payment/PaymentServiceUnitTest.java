package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
}