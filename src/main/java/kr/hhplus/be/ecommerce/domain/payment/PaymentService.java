package kr.hhplus.be.ecommerce.domain.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional
    public void payPayment(PaymentCommand.Payment command) {
        try {
            Payment payment = Payment.create(command.getOrderId(), command.getAmount());
            payment.pay();

            paymentClient.useBalance(command.getUserId(), command.getAmount());
            Optional.ofNullable(command.getUserCouponId())
                .ifPresent(paymentClient::useCoupon);

            paymentRepository.save(payment);

            paymentEventPublisher.paid(
                PaymentEvent.Paid.of(
                    payment.getId(),
                    payment.getOrderId(),
                    command.getUserId(),
                    payment.getAmount()
                )
            );
        } catch (Exception e) {
            paymentEventPublisher.payFailed(PaymentEvent.PayFailed.of(command.getOrderId()));
            throw e;
        }
    }

    @Transactional
    public void cancelPayment(Long paymentId) {
        try {
            Payment payment = paymentRepository.findById(paymentId);
            PaymentInfo.Order order = paymentClient.getOrder(payment.getOrderId());

            paymentClient.refundBalance(order.getUserId(), payment.getAmount());
            if (order.getUserCouponId() != null) {
                paymentClient.cancelCoupon(order.getUserCouponId());
            }

            payment.cancel();

            paymentEventPublisher.canceled(PaymentEvent.Canceled.of(order.getOrderId()));
        } catch (Exception e) {
            log.error("결제 취소 실패 - paymentId: {}", paymentId, e);
            throw e;
        }
    }
}
