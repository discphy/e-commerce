package kr.hhplus.be.ecommerce.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @DisplayName("상품 생성 시, 이름은 필수이다.")
    @Test
    void createWithoutName() {
        // when & then
        assertThatThrownBy(() -> Product.create(null, 1_000L, ProductSellingStatus.SELLING))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 이름은 필수입니다.");
    }

    @DisplayName("상품 생성 시, 가격은 0보다 커야 한다.")
    @Test
    void createWithInvalidPrice() {
        // when & then
        assertThatThrownBy(() -> Product.create("쿠폰명", 0L, ProductSellingStatus.SELLING))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 가격은 0보다 커야 합니다.");
    }

    @DisplayName("상품 생성 시, 판매 상태는 필수이다.")
    @Test
    void createWithoutSellStatus() {
        // when & then
        assertThatThrownBy(() -> Product.create("쿠폰명", 1_000L, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 판매 상태는 필수입니다.");
    }

    @DisplayName("판매 중이지 않는 상품인지 확인한다.")
    @ParameterizedTest
    @ValueSource(strings = {"HOLD", "STOP_SELLING"})
    void cannotSelling(ProductSellingStatus status) {
        // given
        Product product = Product.builder()
            .sellStatus(status)
            .build();

        // when
        boolean result = product.cannotSelling();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("판매 중인 상품인지 확인한다.")
    @ParameterizedTest
    @ValueSource(strings = {"SELLING"})
    void canSelling(ProductSellingStatus status) {
        // given
        Product product = Product.builder()
            .sellStatus(status)
            .build();

        // when
        boolean result = product.cannotSelling();

        // then
        assertThat(result).isFalse();
    }

}