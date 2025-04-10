package kr.hhplus.be.ecommerce.infrastructure.payment;

import kr.hhplus.be.ecommerce.domain.payment.Payment;
import kr.hhplus.be.ecommerce.domain.payment.PaymentRepository;
import org.springframework.stereotype.Component;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    @Override
    public Payment save(Payment payment) {
        return null;
    }
}
