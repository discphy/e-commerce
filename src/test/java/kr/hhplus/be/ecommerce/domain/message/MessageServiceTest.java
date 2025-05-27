package kr.hhplus.be.ecommerce.domain.message;

import kr.hhplus.be.ecommerce.infrastructure.message.client.MessageDataPlatformClient;
import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MessageServiceTest extends IntegrationTestSupport {

    @Autowired
    private MessageService messageService;

    @MockitoBean
    private MessageDataPlatformClient messageDataPlatformClient;

    @DisplayName("주문을 외부 시스템에 전송한다.")
    @Test
    void sendMessage() {
        // given
        MessageCommand.Order command = MessageCommand.Order.builder()
            .orderId(1L)
            .userId(1L)
            .totalPrice(10_000L)
            .discountPrice(1_000L)
            .build();

        // when
        messageService.sendOrder(command);

        // then
        verify(messageDataPlatformClient, times(1)).sendOrder(command);
    }
}