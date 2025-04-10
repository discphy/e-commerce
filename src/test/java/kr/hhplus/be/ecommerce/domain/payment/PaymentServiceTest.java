package kr.hhplus.be.ecommerce.domain.payment;

import kr.hhplus.be.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class PaymentServiceTest extends MockTestSupport {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @DisplayName("결제를 생성하고 저장한다.")
    @Test
    void pay() {
        // given
        PaymentCommand.Payment command = mock(PaymentCommand.Payment.class);

        // when
        paymentService.pay(command);

        // then
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }



}