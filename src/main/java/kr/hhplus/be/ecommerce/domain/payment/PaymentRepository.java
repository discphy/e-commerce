package kr.hhplus.be.ecommerce.domain.payment;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findCompletedPaymentsWithin(List<PaymentStatus> statuses,
                                              LocalDateTime startDateTime,
                                              LocalDateTime endDateTime);
}
