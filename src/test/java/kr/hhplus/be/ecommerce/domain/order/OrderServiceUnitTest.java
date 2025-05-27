package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        OrderCommand.Create command = OrderCommand.Create.of(1L,
            1L,
            0.1,
            List.of(
                OrderCommand.OrderProduct.of(1L, "상품명", 2_000L, 2)
            )
        );

        // when
        OrderInfo.Order order = orderService.createOrder(command);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(3_600L);
        assertThat(order.getDiscountPrice()).isEqualTo(400L);
        verify(orderRepository, times(1)).save(any());
    }

    @DisplayName("결제는 주문이 존재해야 한다.")
    @Test
    void payWithoutOrder() {
        // given
        when(orderRepository.findById(any()))
            .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when
        assertThatThrownBy(() -> orderService.paidOrder(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("주문을 결제한다.")
    @Test
    void paidOrder() {
        // given
        Order order = Order.create(1L,
            1L,
            0.1,
            List.of(
                OrderProduct.create(1L, "상품명", 2_000L, 2)
            )
        );

        when(orderRepository.findById(any()))
            .thenReturn(order);

        // when
        orderService.paidOrder(1L);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @DisplayName("주문을 결제 시, 이벤트를 발행한다.")
    @Test
    void publishEventAfterPaidOrder() {
        // given
        Order order = Order.create(1L,
            1L,
            0.1,
            List.of(
                OrderProduct.create(1L, "상품명", 2_000L, 2)
            )
        );

        when(orderRepository.findById(any()))
            .thenReturn(order);

        // when
        orderService.paidOrder(1L);

        // then
        verify(orderEventPublisher, times(1)).paid(any(OrderEvent.Paid.class));
    }

    @DisplayName("주문을 결제 시, 실패하면 이벤트를 발행하지 않는다.")
    @Test
    void notPublishEventAfterFailedPaidOrder() {
        // given
        when(orderRepository.findById(any()))
            .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when
        assertThatThrownBy(() -> orderService.paidOrder(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");

        // then
        verify(orderEventPublisher, never()).paid(any(OrderEvent.Paid.class));
    }
}