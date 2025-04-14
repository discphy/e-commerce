package kr.hhplus.be.ecommerce.infrastructure.payment;

import kr.hhplus.be.ecommerce.domain.payment.Payment;
import kr.hhplus.be.ecommerce.domain.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPaymentStatusInAndPaidAtBetween(Collection<PaymentStatus> paymentStatuses, LocalDateTime paidAtAfter, LocalDateTime paidAtBefore);
}
