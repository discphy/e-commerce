package kr.hhplus.be.ecommerce.interfaces.rank.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankOrderEventListener {

    private final RankService rankService;

    @Async
    @EventListener
    public void handle(OrderEvent.Completed event) {
        log.info("주문 완료 이벤트 수신 - 랭킹 업데이트");
        rankService.createSellRank(createCommand(event));
    }

    private RankCommand.CreateList createCommand(OrderEvent.Completed event) {
        return RankCommand.CreateList.of(
            event.getOrderProducts().stream()
                .map(op -> RankCommand.Create.of(op.getProductId(), op.getQuantity(), LocalDate.now()))
                .toList()
        );
    }
}
