package kr.hhplus.be.ecommerce.interfaces.message.event;

import kr.hhplus.be.ecommerce.domain.message.MessageService;
import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class MessageOrderEventListenerUnitTest extends MockTestSupport {

    @InjectMocks
    private MessageOrderEventListener messageOrderEventListener;

    @Mock
    private MessageService messageService;

    @DisplayName("주문 완료 시, 외부 데이터 플랫폼으로 주문 정보를 전송한다.")
    @Test
    void handleCompleted() {
        // given
        OrderEvent.Completed event = mock(OrderEvent.Completed.class);

        // when
        messageOrderEventListener.handle(event);

        // then
        verify(messageService, times(1)).sendOrder(any());
    }
}