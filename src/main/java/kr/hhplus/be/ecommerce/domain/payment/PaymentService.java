package kr.hhplus.be.ecommerce.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void pay(PaymentCommand.Payment command) {
        Payment payment = Payment.create(command.getUserId(), command.getOrderId(), command.getAmount());
        payment.pay();

        paymentRepository.save(payment);
    }
}
