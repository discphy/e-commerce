package kr.hhplus.be.ecommerce.interfaces.rank.event;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.support.event.Event;
import kr.hhplus.be.ecommerce.support.event.EventType.GroupId;
import kr.hhplus.be.ecommerce.support.event.EventType.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankOrderMessageEventListener {

    private final RankService rankService;

    @KafkaListener(topics = Topic.ORDER_COMPLETED, groupId = GroupId.ORDER, concurrency = "3")
    public void handleOrderCompleted(String message, Acknowledgment ack) {
        log.info("주문 완료 이벤트 수신 {}", message);

        Event<OrderEvent.Completed> event = Event.of(message, OrderEvent.Completed.class);
        OrderEvent.Completed payload = event.getPayload();

        rankService.createSellRank(createCommand(payload));
        ack.acknowledge();
    }

    private RankCommand.CreateList createCommand(OrderEvent.Completed payload) {
        return RankCommand.CreateList.of(
            payload.getOrderProducts().stream()
                .map(op -> RankCommand.Create.of(op.getProductId(), op.getQuantity(), LocalDate.now()))
                .toList()
        );
    }
}
