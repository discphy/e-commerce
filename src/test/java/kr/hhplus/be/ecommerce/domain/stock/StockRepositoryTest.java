package kr.hhplus.be.ecommerce.domain.stock;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class StockRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("상품 ID로 재고 조회 시, 재고가 존재해야 한다.")
    @Test
    void findByProductIdWhenDoseNotExist() {
        // given
        Long productId = 1L;

        // when & then
        assertThatThrownBy(() -> stockRepository.findByProductId(productId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 존재하지 않습니다.");
    }

    @DisplayName("상품 ID로 재고를 조회한다.")
    @Test
    void findByProductId() {
        // given
        Long productId = 1L;
        Stock stock = Stock.create(productId, 10);
        stockRepository.save(stock);

        // when
        Stock result = stockRepository.findByProductId(productId);

        // then
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getQuantity()).isEqualTo(10);
    }

}