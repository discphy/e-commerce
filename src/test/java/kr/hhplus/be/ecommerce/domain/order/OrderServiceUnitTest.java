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

    @DisplayName("주문 생성 시, 사용자는 존재해야 한다.")
    @Test
    void createOrderWithInvalidUser() {
        // given
        OrderCommand.Create command = OrderCommand.Create.of(
            1L,
            1L,
            List.of(
                OrderCommand.OrderProduct.of(1L, 2)
            )
        );

        when(orderClient.getUser(anyLong()))
            .thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

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

        when(orderClient.getUser(anyLong()))
            .thenReturn(OrderInfo.User.of(1L, "사용자"));

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
    }

    @DisplayName("주문을 생성 시, 이벤트를 발행한다.")
    @Test
    void createOrderWithPublishEvent() {
        // given
        OrderCommand.Create command = OrderCommand.Create.of(
            1L,
            1L,
            List.of(
                OrderCommand.OrderProduct.of(1L, 2)
            )
        );

        when(orderClient.getUser(anyLong()))
            .thenReturn(OrderInfo.User.of(1L, "사용자"));

        when(orderClient.getProducts(any()))
            .thenReturn(List.of(OrderInfo.Product.of(1L, "상품명", 2_000L, 2)));

        when(orderClient.getUsableCoupon(anyLong()))
            .thenReturn(OrderInfo.Coupon.of(1L, 2L, "쿠폰명", 0.1, LocalDateTime.of(2025, 1, 1, 0, 0)));

        // when
        orderService.createOrder(command);

        // then
        verify(orderEventPublisher, times(1)).created(any(OrderEvent.Created.class));
    }

    @DisplayName("결제는 주문이 존재해야 한다.")
    @Test
    void payWithoutOrder() {
        // given
        when(orderRepository.findById(any()))
            .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when
        assertThatThrownBy(() -> orderService.completedOrder(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("주문을 결제한다.")
    @Test
    void completedOrder() {
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
        OrderInfo.Completed completed = orderService.completedOrder(1L);

        // then
        assertThat(completed.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문을 취소한다.")
    @Test
    void cancelOrder() {
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
        orderService.cancelOrder(1L);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @DisplayName("주문을 결제 시, 실패하면 이벤트를 발행하지 않는다.")
    @Test
    void notPublishEventAfterFailedCompletedOrder() {
        // given
        when(orderRepository.findById(any()))
            .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when
        assertThatThrownBy(() -> orderService.completedOrder(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");

        // then
        verify(orderEventPublisher, never()).completed(any(OrderEvent.Completed.class));
    }

    @DisplayName("주문 프로세스를 업데이트 시, 아직 대기중인 프로세스가 존재하면 대기한다.")
    @Test
    void updateProcessPending() {
        // given
        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(1L, OrderProcessStatus.SUCCESS);

        List<OrderProcess> processes = List.of(
            OrderProcess.ofPending(OrderProcessTask.BALANCE_USED),
            OrderProcess.ofPending(OrderProcessTask.COUPON_USED),
            OrderProcess.of(OrderProcessTask.STOCK_DEDUCTED, OrderProcessStatus.SUCCESS)
        );

        when(orderRepository.getProcess(any()))
            .thenReturn(processes);

        // when
        orderService.updateProcess(command);

        // then
        verify(orderEventPublisher, never()).paymentWaited(any(OrderEvent.PaymentWaited.class));
        verify(orderEventPublisher, never()).failed(any(OrderEvent.Failed.class));
    }

    @DisplayName("주문 프로세스를 업데이트 시, 실패한 프로세스가 존재하면 실패 이벤트를 발행한다.")
    @Test
    void updateProcessFailed() {
        // given
        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(1L, OrderProcessStatus.FAILED);

        List<OrderProcess> processes = List.of(
            OrderProcess.of(OrderProcessTask.BALANCE_USED, OrderProcessStatus.SUCCESS),
            OrderProcess.of(OrderProcessTask.COUPON_USED, OrderProcessStatus.SUCCESS),
            OrderProcess.of(OrderProcessTask.STOCK_DEDUCTED, OrderProcessStatus.FAILED)
        );

        Order order = Order.create(1L,
            1L,
            0.1,
            List.of(
                OrderProduct.create(1L, "상품명", 2_000L, 2)
            )
        );

        when(orderRepository.getProcess(any()))
            .thenReturn(processes);

        when(orderRepository.findById(any()))
            .thenReturn(order);

        // when
        orderService.updateProcess(command);

        // then
        verify(orderEventPublisher, times(1)).failed(any(OrderEvent.Failed.class));
        verify(orderEventPublisher, never()).paymentWaited(any(OrderEvent.PaymentWaited.class));
    }

    @DisplayName("주문 프로세스를 업데이트 시, 모든 프로세스가 성공하면 결제 대기 이벤트를 발행한다.")
    @Test
    void updateProcessCompleted() {
        // given
        OrderCommand.Process command = OrderCommand.Process.ofStockDeducted(1L, OrderProcessStatus.SUCCESS);

        List<OrderProcess> processes = List.of(
            OrderProcess.of(OrderProcessTask.BALANCE_USED, OrderProcessStatus.SUCCESS),
            OrderProcess.of(OrderProcessTask.COUPON_USED, OrderProcessStatus.SUCCESS),
            OrderProcess.of(OrderProcessTask.STOCK_DEDUCTED, OrderProcessStatus.SUCCESS)
        );

        Order order = Order.create(1L,
            1L,
            0.1,
            List.of(
                OrderProduct.create(1L, "상품명", 2_000L, 2)
            )
        );

        when(orderRepository.getProcess(any()))
            .thenReturn(processes);

        when(orderRepository.findById(any()))
            .thenReturn(order);

        // when
        orderService.updateProcess(command);

        // then
        verify(orderEventPublisher, never()).failed(any(OrderEvent.Failed.class));
        verify(orderEventPublisher, times(1)).paymentWaited(any(OrderEvent.PaymentWaited.class));
    }
}