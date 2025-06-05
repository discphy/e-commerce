package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
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

    @Mock
    private OrderClient orderClient;

    @DisplayName("주문 생성 시, 유효한 상품만 존재해야 한다.")
    @Test
    void createOrderWithInvalidProduct() {
        // given
        OrderCommand.Create command = OrderCommand.Create.of(
            1L,
            1L,
            List.of(
                OrderCommand.OrderProduct.of(1L, 2)
            )
        );

        when(orderClient.getProducts(any()))
            .thenThrow(new IllegalArgumentException("상품이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품이 존재하지 않습니다.");
    }

    @DisplayName("주문 생성 시, 쿠폰 ID가 있으면 사용가능한 쿠폰이어야 한다.")
    @Test
    void createOrderWithInvalidCoupon() {
        // given
        OrderCommand.Create command = OrderCommand.Create.of(
            1L,
            1L,
            List.of(
                OrderCommand.OrderProduct.of(1L, 2)
            )
        );

        when(orderClient.getUsableCoupon(anyLong()))
            .thenThrow(new IllegalStateException("사용할 수 없는 쿠폰입니다."));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("사용할 수 없는 쿠폰입니다.");
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        OrderCommand.Create command = OrderCommand.Create.of(
            1L,
            1L,
            List.of(
                OrderCommand.OrderProduct.of(1L, 2)
            )
        );

        when(orderClient.getProducts(any()))
            .thenReturn(List.of(OrderInfo.Product.of(1L, "상품명", 2_000L, 2)));

        when(orderClient.getUsableCoupon(anyLong()))
            .thenReturn(OrderInfo.Coupon.of(1L, 2L, "쿠폰명", 0.1, LocalDateTime.of(2025, 1, 1, 0, 0)));

        // when
        OrderInfo.Order order = orderService.createOrder(command);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(3_600L);
        assertThat(order.getDiscountPrice()).isEqualTo(400L);
        verify(orderRepository, times(1)).save(any());
        verify(orderClient, times(1)).deductStock(any());
        verify(orderEventPublisher, times(1)).created(any(OrderEvent.Created.class));
    }

    @DisplayName("주문 완료 시, 주문이 존재해야 한다.")
    @Test
    void completedOrderWithoutOrder() {
        // given
        when(orderRepository.findById(any()))
            .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> orderService.completedOrder(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
        verify(orderEventPublisher).completeFailed(any(OrderEvent.CompleteFailed.class));
    }

    @DisplayName("주문을 완료한다.")
    @Test
    void completedOrder() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품명", 2_000L, 2)
        ));

        when(orderRepository.findById(any()))
            .thenReturn(order);

        // when
        orderService.completedOrder(1L);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(order.getCompletedAt()).isNotNull();
        verify(orderEventPublisher, times(1)).completed(any(OrderEvent.Completed.class));
    }

    @DisplayName("주문 취소 시, 주문이 존재해야 한다.")
    @Test
    void cancelOrderWithoutOrder() {
        // given
        when(orderRepository.findById(any()))
            .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("주문 취소 시, 재고를 복구에 실패하면 예외가 발생한다.")
    @Test
    void cancelOrderRestoreStockFailed() {
        // given
        Order order = mock(Order.class);

        when(orderRepository.findById(any()))
            .thenReturn(order);

        doThrow(new IllegalStateException("재고 복구에 실패했습니다."))
            .when(orderClient).restoreStock(any());

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("재고 복구에 실패했습니다.");
    }

    @DisplayName("주문을 취소한다.")
    @Test
    void cancelOrder() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품명", 2_000L, 2)
        ));

        when(orderRepository.findById(any()))
            .thenReturn(order);

        // when
        orderService.cancelOrder(1L);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @DisplayName("주문 조회 시, 주문이 존재해야 한다.")
    @Test
    void getOrderWithoutOrder() {
        // given
        when(orderRepository.findById(any()))
            .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("주문을 조회한다.")
    @Test
    void getOrder() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품명", 2_000L, 2)
        ));

        when(orderRepository.findById(any()))
            .thenReturn(order);

        // when
        OrderInfo.Order orderInfo = orderService.getOrder(1L);

        // then
        assertThat(orderInfo.getOrderId()).isEqualTo(order.getId());
        assertThat(orderInfo.getTotalPrice()).isEqualTo(3_600L);
    }
}