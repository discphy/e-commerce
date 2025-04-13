package kr.hhplus.be.ecommerce.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProductTest {

    @DisplayName("주문 상품의 가격을 계산한다.")
    @Test
    void getPrice() {
        // when
        OrderProduct orderProduct = OrderProduct.create(1L, "상품명", 1000, 1);

        // then
        assertThat(orderProduct.getPrice()).isEqualTo(1000);
    }

}