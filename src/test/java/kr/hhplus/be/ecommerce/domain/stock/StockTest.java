package kr.hhplus.be.ecommerce.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StockTest {

    @DisplayName("재고 생성 시, 재고 수량은 0 이상이어야 한다.")
    @Test
    void createWithInvalidQuantity() {
        // when & then
        assertThatThrownBy(() -> Stock.create(1L, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고 수량은 0 이상이어야 합니다.");
    }

    @DisplayName("재고 차감 시, 재고는 충분해야 한다.")
    @Test
    void deductWithInsufficientQuantity() {
        // given
        Stock stock = Stock.create(1L, 10);
        int quantity = 11;

        // when
        assertThatThrownBy(() -> stock.deduct(quantity))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }

    @DisplayName("재고를 차감한다.")
    @Test
    void deduct() {
        // given
        Stock stock = Stock.create(1L, 10);
        int quantity = 10;

        // when
        stock.deduct(quantity);

        // then
        assertThat(stock.getQuantity()).isZero();
    }

}