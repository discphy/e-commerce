package kr.hhplus.be.ecommerce.payment.infrastructure;

import kr.hhplus.be.ecommerce.payment.domain.Payment;
import kr.hhplus.be.ecommerce.payment.domain.PaymentRepository;
import kr.hhplus.be.ecommerce.payment.infrastructure.jpa.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCoreRepository implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment findById(Long id) {
        return paymentJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));
    }
}
