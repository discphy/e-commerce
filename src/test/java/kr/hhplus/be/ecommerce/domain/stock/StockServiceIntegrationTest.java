package kr.hhplus.be.ecommerce.domain.stock;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class StockServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("재고가 없으면 상품 ID로 재고를 조회할 수 없다.")
    @Test
    void getStockCannotDoseNotExistStock() {
        // given
        Long productId = 1L;

        // when & then
        assertThatThrownBy(() -> stockService.getStock(productId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 존재하지 않습니다.");
    }

    @DisplayName("상품 ID로 재고를 조회할 수 있다.")
    @Test
    void getStock() {
        // given
        Long productId = 1L;
        Stock stock = Stock.create(productId, 10);
        stockRepository.save(stock);

        // when
        StockInfo.Stock stockInfo = stockService.getStock(productId);

        // then
        assertThat(stockInfo.getStockId()).isEqualTo(stock.getId());
        assertThat(stockInfo.getQuantity()).isEqualTo(stock.getQuantity());
    }

    @DisplayName("재고가 없으면 상품 ID로 재고를 차감할 수 없다.")
    @Test
    void deductStockCannotDoseNotExistStock() {
        // given
        Long productId = 1L;

        List<StockCommand.OrderProduct> orderProducts = List.of(StockCommand.OrderProduct.of(productId, 100));
        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(orderProducts);

        // when & then
        assertThatThrownBy(() -> stockService.deductStock(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 존재하지 않습니다.");
    }

    @DisplayName("재고가 부족하면 상품 ID로 재고를 차감할 수 없다.")
    @Test
    void deductStockCannotInsufficientStockQuantity() {
        // given
        Long productId = 1L;
        Stock stock = Stock.create(productId, 1);
        stockRepository.save(stock);

        List<StockCommand.OrderProduct> orderProducts = List.of(StockCommand.OrderProduct.of(productId, 3));
        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(orderProducts);

        // when & then
        assertThatThrownBy(() -> stockService.deductStock(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }

    @DisplayName("상품 ID 리스트로 재고를 차감할 수 있다.")
    @Test
    void deductStock() {
        // given
        Long productId1 = 1L;
        Long productId2 = 2L;
        Stock stock1 = Stock.create(productId1, 10);
        Stock stock2 = Stock.create(productId2, 3);

        stockRepository.save(stock1);
        stockRepository.save(stock2);

        List<StockCommand.OrderProduct> orderProducts = List.of(
            StockCommand.OrderProduct.of(productId1, 3),
            StockCommand.OrderProduct.of(productId2, 3)
        );
        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(orderProducts);

        // when
        stockService.deductStock(command);

        // then
        Stock updatedStock1 = stockRepository.findByProductId(productId1);
        assertThat(updatedStock1.getQuantity()).isEqualTo(7);

        Stock updatedStock2 = stockRepository.findByProductId(productId2);
        assertThat(updatedStock2.getQuantity()).isZero();
    }
}