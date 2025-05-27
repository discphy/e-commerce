package kr.hhplus.be.ecommerce.domain.message;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MessageServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageClient messageClient;

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
        verify(messageClient, times(1)).sendOrder(any());
    }

}