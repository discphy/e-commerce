package kr.hhplus.be.ecommerce.domain.payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment findById(Long id);

}
