package kr.hhplus.be.ecommerce.infrastructure.payment;

import kr.hhplus.be.ecommerce.domain.payment.Payment;
import kr.hhplus.be.ecommerce.domain.payment.PaymentRepository;
import kr.hhplus.be.ecommerce.domain.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id);
    }

    @Override
    public List<Payment> findCompletedPaymentsWithIn(List<PaymentStatus> statuses,
                                                     LocalDateTime startDateTime,
                                                     LocalDateTime endDateTime) {
        return paymentJpaRepository.findByPaymentStatusInAndPaidAtBetween(statuses, startDateTime, endDateTime);
    }

}
