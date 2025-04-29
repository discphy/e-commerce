package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

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

    @DisplayName("결제 완료 된 상품을 요청한 날짜에 조회한다.")
    @Test
    void getPaidProducts() {
        // given
        Order order1 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(2L, "상품2", 20_000L, 3)
        ));
        Order order2 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(3L, "상품3", 30_000L, 4)
        ));
        Order order3 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(2L, "상품2", 20_000L, 3),
            OrderProduct.create(3L, "상품3", 30_000L, 4)
        ));

        List.of(order1, order2, order3)
            .forEach(order -> {
                order.paid(LocalDateTime.of(2025, 4, 22, 12, 0, 0));
                orderRepository.save(order);
            });

        OrderCommand.DateQuery command = OrderCommand.DateQuery.of(LocalDate.of(2025, 4, 23));

        // when
        OrderInfo.PaidProducts result = orderService.getPaidProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(3)
            .extracting("productId", "quantity")
            .containsExactlyInAnyOrder(
                tuple(1L, 4),
                tuple(2L, 6),
                tuple(3L, 8)
            );
    }
}