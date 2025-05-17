package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("주문을 저장한다.")
    @Test
    void save() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품명", 1_000L, 1)
        ));

        // when
        Order result = orderRepository.save(order);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUserCouponId()).isEqualTo(1L);
        assertThat(result.getDiscountPrice()).isEqualTo(100L);
        assertThat(result.getTotalPrice()).isEqualTo(900L);
        assertThat(result.getOrderProducts()).hasSize(1);
    }

    @DisplayName("주문이 존재하지 않으면, 주문 ID로 주문을 찾을 수 없다.")
    @Test
    void findByIdWhenDoseNotExist() {
        // given
        Long orderId = 1L;

        // when & then
        assertThatThrownBy(() -> orderRepository.findById(orderId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문이 존재하지 않습니다.");
    }

    @DisplayName("주문 ID로 주문을 찾는다.")
    @Test
    void findById() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품명", 1_000L, 1)
        ));
        orderRepository.save(order);

        // when
        Order result = orderRepository.findById(order.getId());

        // then
        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getUserId()).isEqualTo(order.getUserId());
        assertThat(result.getUserCouponId()).isEqualTo(order.getUserCouponId());
        assertThat(result.getDiscountPrice()).isEqualTo(order.getDiscountPrice());
        assertThat(result.getTotalPrice()).isEqualTo(order.getTotalPrice());
    }

    @DisplayName("주문 ID 리스트로 주문 상품을 찾는다.")
    @Test
    void findOrderIdsIn() {
        // given
        Order order1 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품명", 1_000L, 1)
        ));
        Order order2 = Order.create(2L, 1L, 0.1, List.of(
            OrderProduct.create(2L, "상품명", 1_000L, 1)
        ));
        orderRepository.save(order1);
        orderRepository.save(order2);

        List<Long> orderIds = List.of(order1.getId(), order2.getId());

        // when
        List<OrderProduct> result = orderRepository.findOrderIdsIn(orderIds);

        // then
        assertThat(result).hasSize(2)
            .extracting(OrderProduct::getOrder)
            .containsExactlyInAnyOrder(order1, order2);
    }
}