package kr.hhplus.be.ecommerce.payment.domain;


public interface PaymentRepository {

    Payment save(Payment payment);

    Payment findById(Long id);

}
