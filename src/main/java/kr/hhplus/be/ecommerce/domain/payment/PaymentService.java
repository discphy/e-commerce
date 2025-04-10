package kr.hhplus.be.ecommerce.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void pay(PaymentCommand.Payment command) {
        Payment payment = Payment.create(command.getOrderId(), command.getAmount());
        payment.pay();

        paymentRepository.save(payment);
    }

    public PaymentInfo.Orders getCompletedOrdersBetweenDays(int recentDays) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusDays(recentDays);

        List<Payment> completedPayments = paymentRepository
            .findPaymentStatusInAndBetweenPaidAt(PaymentStatus.forCompleted(), startDateTime, endDateTime);

        return PaymentInfo.Orders.of(completedPayments.stream()
            .map(Payment::getOrderId)
            .toList());
    }
}
