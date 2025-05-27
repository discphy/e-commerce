package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@Transactional
@RecordApplicationEvents
class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ApplicationEvents events;

    @MockitoSpyBean
    private OrderEventPublisher orderEventPublisher;

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        OrderCommand.Create command = OrderCommand.Create.of(1L, 1L, 0.1, List.of(
            OrderCommand.OrderProduct.of(1L, "상품1", 10_000L, 2),
            OrderCommand.OrderProduct.of(2L, "상품2", 20_000L, 3)
        ));

        // when
        OrderInfo.Order order = orderService.createOrder(command);

        // then
        assertThat(order.getOrderId()).isNotNull();
        assertThat(order.getTotalPrice()).isEqualTo(72_000L);
        assertThat(order.getDiscountPrice()).isEqualTo(8_000L);
    }

    @DisplayName("주문을 결제완료처리 한다.")
    @Test
    void paidOrder() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(2L, "상품2", 20_000L, 3)
        ));
        orderRepository.save(order);

        // when
        orderService.paidOrder(order.getId());

        // then
        Order result = orderRepository.findById(order.getId());
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @DisplayName("주문을 결제 시, 이벤트를 발행한다.")
    @Test
    void publishEventAfterPaidOrder() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(2L, "상품2", 20_000L, 3)
        ));
        orderRepository.save(order);

        // when
        orderService.paidOrder(order.getId());

        // then
        verify(orderEventPublisher, times(1)).paid(any(OrderEvent.Paid.class));
        assertThat(events.stream(OrderEvent.Paid.class).count()).isEqualTo(1);
    }

    @DisplayName("주문을 결제 시, 실패하면 이벤트를 발행하지 않는다.")
    @Test
    void notPublishEventAfterFailedPaidOrder() {
        // given
        Long notExistOrderId = -1L;

        // when
        assertThatThrownBy(() -> orderService.paidOrder(notExistOrderId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");

        // then
        verify(orderEventPublisher, never()).paid(any(OrderEvent.Paid.class));
        assertThat(events.stream(OrderEvent.Paid.class).count()).isZero();
    }
}