package kr.hhplus.be.ecommerce.infrastructure.order.external;

import kr.hhplus.be.ecommerce.domain.order.Order;
import kr.hhplus.be.ecommerce.domain.order.OrderExternalClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderExternalClientImpl implements OrderExternalClient {

    @Override
    public void sendOrderMessage(Order order) {
        log.info("외부 데이터 플랫폼 주문정보 저장 : {}", order);
    }
}
