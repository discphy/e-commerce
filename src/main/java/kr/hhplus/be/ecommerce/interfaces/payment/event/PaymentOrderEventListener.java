package kr.hhplus.be.ecommerce.interfaces.payment.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.payment.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOrderEventListener {

    private final PaymentService paymentService;
    private final PaymentEventPublisher paymentEventPublisher;

    @Async
    @EventListener
    public void handle(OrderEvent.PaymentWaited event) {
        log.info("주문 결제 대기 이벤트 수신 - 결제 진행");
        try {
            PaymentInfo.Payment payment = paymentService.pay(createCommand(event));
            paymentEventPublisher.paid(createPaidEvent(event, payment.getPaymentId()));
        } catch (Exception e) {
            log.error("주문 결제 대기 이벤트 수신 - 결제 진행 에러", e);
            paymentEventPublisher.payFailed(createPayFailedEvent(event));
        }
    }

    @Async
    @EventListener
    public void handle(OrderEvent.CompleteFailed event) {
        log.info("주문 완료 실패 이벤트 수신 - 결제 취소");
        try {
            paymentService.cancelPayment(event.getPaymentId());
            paymentEventPublisher.canceled(createCanceledEvent(event));
        } catch (Exception e) {
            log.error("주문 완료 실패 이벤트 수신 - 결제 취소 에러", e);
        }
    }

    private PaymentCommand.Payment createCommand(OrderEvent.PaymentWaited event) {
        return PaymentCommand.Payment.of(event.getOrderId(), event.getUserId(), event.getTotalPrice());
    }

    private PaymentEvent.Paid createPaidEvent(OrderEvent.PaymentWaited event, Long paymentId) {
        return PaymentEvent.Paid.builder()
            .paymentId(paymentId)
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> PaymentEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build()
                ).toList()
            ).build();
    }

    private PaymentEvent.PayFailed createPayFailedEvent(OrderEvent.PaymentWaited event) {
        return PaymentEvent.PayFailed.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> PaymentEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build()
                ).toList()
            ).build();
    }

    private PaymentEvent.Canceled createCanceledEvent(OrderEvent.CompleteFailed event) {
        return PaymentEvent.Canceled.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> PaymentEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build()
                ).toList()
            ).build();
    }
}
