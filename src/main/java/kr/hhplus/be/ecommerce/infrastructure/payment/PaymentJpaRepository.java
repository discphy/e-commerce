package kr.hhplus.be.ecommerce.infrastructure.payment;

import kr.hhplus.be.ecommerce.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

}
