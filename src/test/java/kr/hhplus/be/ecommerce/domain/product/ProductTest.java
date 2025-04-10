package kr.hhplus.be.ecommerce.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

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