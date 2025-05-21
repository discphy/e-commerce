package kr.hhplus.be.ecommerce.infrastructure.message.client;

import kr.hhplus.be.ecommerce.domain.message.MessageCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageDataPlatformClient {

    public void sendOrder(MessageCommand.Order message) {
        log.info("외부 데이터 플랫폼 주문 정보 전송 : {}", message);
    }
}
