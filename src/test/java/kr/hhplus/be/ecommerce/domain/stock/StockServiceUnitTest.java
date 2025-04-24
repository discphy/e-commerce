package kr.hhplus.be.ecommerce.domain.stock;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @DisplayName("유효한 ID로 재고를 차감해야 한다.")
    @Test
    void deductStockWithInvalidId() {
        // given
        StockCommand.OrderProducts command = mock(StockCommand.OrderProducts.class);
        StockCommand.OrderProduct orderProduct = mock(StockCommand.OrderProduct.class);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct, orderProduct));

        when(stockRepository.findByProductIdWithLock(anyLong()))
            .thenThrow(new IllegalArgumentException("재고가 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> stockService.deductStock(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 존재하지 않습니다.");
    }

    @DisplayName("재고가 충분해야 차감할 수 있다. ")
    @Test
    void deductStockWithInsufficientStock() {
        // given
        StockCommand.OrderProducts command = mock(StockCommand.OrderProducts.class);
        StockCommand.OrderProduct orderProduct = mock(StockCommand.OrderProduct.class);

        when(orderProduct.getQuantity())
            .thenReturn(1);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct, orderProduct));

        when(stockRepository.findByProductIdWithLock(anyLong()))
            .thenReturn(Stock.create(1L, 0));

        // when
        assertThatThrownBy(() -> stockService.deductStock(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("재고가 부족합니다.");
    }

    @DisplayName("재고를 차감한다.")
    @Test
    void deductStock() {
        // given
        StockCommand.OrderProducts command = mock(StockCommand.OrderProducts.class);
        StockCommand.OrderProduct orderProduct = mock(StockCommand.OrderProduct.class);

        when(orderProduct.getQuantity())
            .thenReturn(10);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct));

        Stock stock = Stock.create(1L, 10);
        when(stockRepository.findByProductIdWithLock(anyLong()))
            .thenReturn(stock, stock);

        // when
        stockService.deductStock(command);

        // then
         assertThat(stock.getQuantity()).isZero();
    }

    @DisplayName("상품 ID로 재고를 조회한다.")
    @Test
    void getStock() {
        // given
        when(stockRepository.findByProductId(anyLong()))
            .thenReturn(Stock.create(1L, 10));

        // when
        StockInfo.Stock stock = stockService.getStock(1L);

        // then
        assertThat(stock.getQuantity()).isEqualTo(10);
    }
}