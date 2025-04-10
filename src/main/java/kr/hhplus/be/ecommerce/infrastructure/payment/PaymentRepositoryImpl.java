package kr.hhplus.be.ecommerce.infrastructure.payment;

import kr.hhplus.be.ecommerce.domain.payment.Payment;
import kr.hhplus.be.ecommerce.domain.payment.PaymentRepository;
import kr.hhplus.be.ecommerce.domain.payment.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    @Override
    public Payment save(Payment payment) {
        return null;
    }

    @Override
    public List<Payment> findPaymentStatusInAndBetweenPaidAt(List<PaymentStatus> statuses, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return List.of();
    }

}
