package kr.hhplus.be.ecommerce.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @DisplayName("주문 상품이 없는 주문을 생성할 수 없다.")
    @Test
    void createWithoutOrderProducts() {
        // when & then
        assertThatThrownBy(() -> Order.create(1L, null, 0.0, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 상품이 없습니다.");
    }

    @DisplayName("주문 상품이 비어있는 주문을 생성할 수 없다.")
    @Test
    void createEmptyOrderProducts() {
        // when & then
        assertThatThrownBy(() -> Order.create(1L, null, 0.0, List.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 상품이 없습니다.");
    }

    @DisplayName("할인이 없는 주문을 생성한다.")
    @Test
    void createWithoutDiscount() {
        // given
        List<OrderProduct> orderProducts = List.of(
            OrderProduct.create(1L, "상품명1", 1000, 1),
            OrderProduct.create(2L, "상품명2", 2000, 2),
            OrderProduct.create(3L, "상품명3", 3000, 3),
            OrderProduct.create(4L, "상품명4", 4000, 4)
        );

        // when
        Order order = Order.create(1L, null, 0.0, orderProducts);

        // then
        assertThat(order.getDiscountPrice()).isZero();
        assertThat(order.getTotalPrice()).isEqualTo(1000 + 2000 * 2 + 3000 * 3 + 4000 * 4);
    }

    @DisplayName("할인이 있는 주문을 생성한다.")
    @Test
    void createWithDiscount() {
        // given
        List<OrderProduct> orderProducts = List.of(
            OrderProduct.create(1L, "상품명1", 1000, 1),
            OrderProduct.create(2L, "상품명2", 2000, 2),
            OrderProduct.create(3L, "상품명3", 3000, 3),
            OrderProduct.create(4L, "상품명4", 4000, 4)
        );

        // when
        Order order = Order.create(1L, 2L, 0.1, orderProducts);

        // then
        long expectedTotalPrice = 1000 + 2000 * 2 + 3000 * 3 + 4000 * 4;
        long expectedDiscountPrice = (long) (expectedTotalPrice * 0.1);
        assertThat(order.getDiscountPrice()).isEqualTo(expectedDiscountPrice);
        assertThat(order.getTotalPrice()).isEqualTo(expectedTotalPrice - expectedDiscountPrice);
    }

}