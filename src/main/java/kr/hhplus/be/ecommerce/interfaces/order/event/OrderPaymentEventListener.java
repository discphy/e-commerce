package kr.hhplus.be.ecommerce.interfaces.order.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderEventPublisher;
import kr.hhplus.be.ecommerce.domain.order.OrderProcesses;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.payment.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentEventListener {

    private final OrderService orderService;
    private final OrderEventPublisher orderEventPublisher;

    @Async
    @EventListener
    public void handle(PaymentEvent.Paid event) {
        log.info("결제 완료 이벤트 수신 - 주문 완료");
        try {
            orderService.completedOrder(event.getOrderId());
            orderEventPublisher.completed(createCompleteEvent(event));
        } catch (Exception e) {
            log.error("결제 완료 이벤트 수신 - 주문 완료 에러", e);
            orderEventPublisher.completeFailed(createCompleteFailedEvent(event));
        }
    }

    @Async
    @EventListener
    public void handle(PaymentEvent.Canceled event) {
        log.info("결제 취소 이벤트 수신 - 주문 실패");
        try {
            orderEventPublisher.failed(createFailedEvent(event));
        } catch (Exception e) {
            log.error("결제 취소 이벤트 수신 - 주문 실패 에러", e);
        }
    }

    @Async
    @EventListener
    public void handle(PaymentEvent.PayFailed event) {
        log.info("결제 실패 이벤트 수신 - 주문 실패");
        try {
            orderEventPublisher.failed(createFailedEvent(event));
        } catch (Exception e) {
            log.error("결제 실패 이벤트 수신 - 주문 실패 에러", e);
        }
    }

    private OrderEvent.Completed createCompleteEvent(PaymentEvent.Paid event) {
        return OrderEvent.Completed.builder()
            .paymentId(event.getPaymentId())
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> OrderEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build()
                ).toList()
            ).build();
    }

    private OrderEvent.CompleteFailed createCompleteFailedEvent(PaymentEvent.Paid event) {
        return OrderEvent.CompleteFailed.builder()
            .paymentId(event.getPaymentId())
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> OrderEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build()
                ).toList()
            ).build();
    }

    private OrderEvent.Failed createFailedEvent(PaymentEvent.Canceled event) {
        return OrderEvent.Failed.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> OrderEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build())
                .toList())
            .processes(OrderProcesses.ofFailed())
            .build();
    }

    private OrderEvent.Failed createFailedEvent(PaymentEvent.PayFailed event) {
        return OrderEvent.Failed.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> OrderEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build())
                .toList())
            .processes(OrderProcesses.ofFailed())
            .build();
    }
}
