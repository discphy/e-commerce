package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.domain.outbox.OutboxEvent;
import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderClient orderClient;

    @MockitoSpyBean
    private OrderEventPublisher orderEventPublisher;

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
        verify(orderClient).deductStock(anyList());
        verify(orderEventPublisher, times(1)).created(any(OrderEvent.Created.class));
        assertThat(events.stream(OutboxEvent.Auto.class).count()).isEqualTo(1);
    }

    @DisplayName("주문 완료 시, 주문이 존재해야 한다.")
    @Test
    void completedOrderWithoutOrder() {
        // given
        Long orderId = -1L;

        // when & then
        assertThatThrownBy(() -> orderService.completedOrder(orderId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("주문을 완료한다.")
    @Test
    void completedOrder() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(2L, "상품2", 20_000L, 3)
        ));
        orderRepository.save(order);

        // when
        orderService.completedOrder(order.getId());

        // then
        Order result = orderRepository.findById(order.getId());
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        verify(orderEventPublisher, times(1)).completed(any(OrderEvent.Completed.class));
        assertThat(events.stream(OutboxEvent.Auto.class).count()).isEqualTo(1);
    }

    @DisplayName("주문 취소 시, 주문이 존재해야 한다.")
    @Test
    void cancelOrderWithoutOrder() {
        // given
        Long orderId = -1L;

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("주문 취소 시, 재고를 복구에 실패하면 예외가 발생한다.")
    @Test
    void cancelOrderRestoreStockFailed() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(2L, "상품2", 20_000L, 3)
        ));
        orderRepository.save(order);

        doThrow(new IllegalStateException("재고 복구에 실패했습니다."))
            .when(orderClient).restoreStock(any());

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(order.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("재고 복구에 실패했습니다.");
    }

    @DisplayName("주문을 취소한다.")
    @Test
    void cancelOrder() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(2L, "상품2", 20_000L, 3)
        ));
        orderRepository.save(order);

        // when
        orderService.cancelOrder(order.getId());

        // then
        Order result = orderRepository.findById(order.getId());
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }
}