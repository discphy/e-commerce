package kr.hhplus.be.ecommerce.interfaces.stock.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderProcessTask;
import kr.hhplus.be.ecommerce.domain.stock.StockCommand;
import kr.hhplus.be.ecommerce.domain.stock.StockEvent;
import kr.hhplus.be.ecommerce.domain.stock.StockEventPublisher;
import kr.hhplus.be.ecommerce.domain.stock.StockService;
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
public class StockOrderEventListener {

    private final StockService stockService;
    private final StockEventPublisher stockEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderEvent.Created event) {
        log.info("주문 생성 이벤트 수신 - 재고 차감");
        try {
            stockService.deductStock(createCommand(event));
            stockEventPublisher.deducted(createDeductedEvent(event));
        } catch (Exception e) {
            log.error("주문 생성 이벤트 수신 - 재고 차감 에러", e);
            stockEventPublisher.deductFailed(createDeductFailedEvent(event));
        }
    }

    @Async
    @EventListener
    public void handle(OrderEvent.Failed event) {
        log.info("주문 실패 이벤트 수신 - 재고 복구");
        if (event.getProcesses().isSuccess(OrderProcessTask.STOCK_DEDUCTED)) {
            log.info("주문 실패 이벤트 수신 - 재고 복구 수행");
            stockService.restoreStock(createCommand(event));
        }
    }

    private StockCommand.Deduct createCommand(OrderEvent.Created event) {
        return StockCommand.Deduct.of(
            event.getOrderProducts().stream()
                .map(p -> StockCommand.OrderProduct.of(p.getProductId(), p.getQuantity()))
                .toList()
        );
    }

    private StockCommand.Restore createCommand(OrderEvent.Failed event) {
        return StockCommand.Restore.of(
            event.getOrderProducts().stream()
                .map(p -> StockCommand.OrderProduct.of(p.getProductId(), p.getQuantity()))
                .toList()
        );
    }

    private StockEvent.Deducted createDeductedEvent(OrderEvent.Created event) {
        return StockEvent.Deducted.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> StockEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build()
                ).toList()
            ).build();
    }

    private StockEvent.DeductFailed createDeductFailedEvent(OrderEvent.Created event) {
        return StockEvent.DeductFailed.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .build();
    }
}
