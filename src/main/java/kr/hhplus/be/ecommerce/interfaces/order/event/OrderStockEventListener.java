package kr.hhplus.be.ecommerce.interfaces.order.event;

import kr.hhplus.be.ecommerce.domain.order.OrderCommand;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.stock.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.ecommerce.domain.order.OrderProcessStatus.FAILED;
import static kr.hhplus.be.ecommerce.domain.order.OrderProcessStatus.SUCCESS;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStockEventListener {

    private final OrderService orderService;

    @Async
    @EventListener
    public void handle(StockEvent.Deducted event) {
        log.info("재고 차감 성공 이벤트 수신 - 주문 프로세스 성공 갱신");
        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(event.getOrderId(), SUCCESS);
        orderService.updateProcess(command);
    }

    @Async
    @EventListener
    public void handle(StockEvent.DeductFailed event) {
        log.info("재고 차감 실패 이벤트 수신 - 주문 프로세스 실패 갱신");
        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(event.getOrderId(), FAILED);
        orderService.updateProcess(command);
    }

}
