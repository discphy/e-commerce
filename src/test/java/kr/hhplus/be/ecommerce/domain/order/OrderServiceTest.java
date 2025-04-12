package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    @DisplayName("주문을 결제한다. 결제 완료 시, 외부 데이터 플랫폼으로 주문정보를 전송한다.")
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
        verify(orderRepository, times(1)).sendOrderMessage(order);
    }

    @DisplayName("상위 상품을 조회한다.")
    @Test
    void getTopPaidProducts() {
        // given
        List<OrderProduct> orderProducts = List.of(
            OrderProduct.create(1L, "상품명", 2_000L, 2),
            OrderProduct.create(2L, "상품명", 3_000L, 3),
            OrderProduct.create(1L, "상품명", 2_000L, 4),
            OrderProduct.create(3L, "상품명", 2_000L, 3),
            OrderProduct.create(4L, "상품명", 2_000L, 2),
            OrderProduct.create(5L, "상품명", 2_000L, 1)
        );

        when(orderRepository.findOrderIdsIn(any()))
            .thenReturn(orderProducts);

        OrderCommand.TopOrders command = OrderCommand.TopOrders.of(List.of(1L, 2L), 5);

        // when
        OrderInfo.TopPaidProducts topPaidProducts = orderService.getTopPaidProducts(command);

        // then
        assertThat(topPaidProducts.getProductIds()).hasSize(5)
            .containsExactly(1L, 2L, 3L, 4L, 5L);
    }
}