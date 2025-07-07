package kr.hhplus.be.ecommerce.payment.infrastructure.jpa;

import kr.hhplus.be.ecommerce.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

}
