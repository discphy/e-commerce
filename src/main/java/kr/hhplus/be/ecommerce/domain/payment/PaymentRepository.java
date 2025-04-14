package kr.hhplus.be.ecommerce.domain.payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    List<Payment> findCompletedPaymentsWithIn(List<PaymentStatus> statuses,
                                              LocalDateTime startDateTime,
                                              LocalDateTime endDateTime);
}
