package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderServiceTest extends MockTestSupport {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        OrderCommand.Create command = mock(OrderCommand.Create.class);

        // when
        orderService.createOrder(command);

        // then
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @DisplayName("주문의 상태를 결제완료로 변경한다.")
    @Test
    void paidOrder() {
        // given
        Order order = Order.create(1L, 1L, 0, List.of());

        when(orderRepository.findById(anyLong()))
            .thenReturn(order);

        // when
        orderService.paidOrder(1L);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository, times(1)).sendOrderMessage(order);
    }
}