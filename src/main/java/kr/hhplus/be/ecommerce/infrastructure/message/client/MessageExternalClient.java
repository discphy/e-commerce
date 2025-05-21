package kr.hhplus.be.ecommerce.infrastructure.message.client;

import kr.hhplus.be.ecommerce.domain.message.MessageClient;
import kr.hhplus.be.ecommerce.domain.message.MessageCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageExternalClient implements MessageClient {

    private final MessageDataPlatformClient messageDataPlatformClient;

    @Override
    public void sendOrder(MessageCommand.Order message) {
        messageDataPlatformClient.sendOrder(message);
    }
}
