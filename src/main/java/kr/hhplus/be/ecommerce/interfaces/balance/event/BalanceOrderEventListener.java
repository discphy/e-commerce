package kr.hhplus.be.ecommerce.interfaces.balance.event;

import kr.hhplus.be.ecommerce.domain.balance.BalanceCommand;
import kr.hhplus.be.ecommerce.domain.balance.BalanceEvent;
import kr.hhplus.be.ecommerce.domain.balance.BalanceEventPublisher;
import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderProcessTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceOrderEventListener {

    private final BalanceService balanceService;
    private final BalanceEventPublisher balanceEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderEvent.Created event) {
        log.info("주문 생성 이벤트 수신 - 잔액 사용");
        try {
            balanceService.useBalance(BalanceCommand.Use.of(event.getUserId(), event.getTotalPrice()));
            balanceEventPublisher.used(createUsedEvent(event));
        } catch (Exception e) {
            log.error("주문 생성 이벤트 수신 - 잔액 사용 에러", e);
            balanceEventPublisher.useFailed(createUseFailedEvent(event));
        }
    }

    @Async
    @EventListener
    public void handle(OrderEvent.Failed event) {
        log.info("주문 실패 이벤트 수신 - 잔액 환불");
        if (event.getProcesses().isSuccess(OrderProcessTask.BALANCE_USED)) {
            log.info("주문 실패 이벤트 수신 - 잔액 환불 취소 수행");
            balanceService.refundBalance(BalanceCommand.Refund.of(event.getUserId(), event.getTotalPrice()));
        }
    }

    private BalanceEvent.Used createUsedEvent(OrderEvent.Created event) {
        return BalanceEvent.Used.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> BalanceEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build()
                ).toList()
            ).build();
    }

    private BalanceEvent.UseFailed createUseFailedEvent(OrderEvent.Created event) {
        return BalanceEvent.UseFailed.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .build();
    }
}
