package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderExternalClient orderExternalClient;

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
        verify(orderExternalClient, times(1)).sendOrderMessage(order);
    }

    @DisplayName("결제 완료 된 상품을 요청한 날짜에 조회한다.")
    @Test
    void getPaidProducts() {
        // given
        OrderCommand.DateQuery command = OrderCommand.DateQuery.of(LocalDate.of(2025, 4, 23));

        List<OrderInfo.PaidProduct> paidProducts = List.of(
            OrderInfo.PaidProduct.of(1L, 2),
            OrderInfo.PaidProduct.of(2L, 4)
        );

        when(orderRepository.findPaidProducts(any()))
            .thenReturn(paidProducts);

        // when
        OrderInfo.PaidProducts result = orderService.getPaidProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(2)
            .extracting("productId", "quantity")
            .containsExactlyInAnyOrder(
                tuple(1L, 2),
                tuple(2L, 4)
            );
    }
}