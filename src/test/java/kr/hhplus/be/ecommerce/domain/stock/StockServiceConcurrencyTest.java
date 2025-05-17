package kr.hhplus.be.ecommerce.domain.stock;

import kr.hhplus.be.ecommerce.test.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class StockServiceConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("동시에 재고를 차감하는 경우 모든 요청에 대해 차감 되어야 한다.")
    @Test
    void deductStockWithPessimisticWriteLock() {
        // given
        Stock stock = Stock.create(1L, 10);
        stockRepository.save(stock);

        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(
            List.of(
                StockCommand.OrderProduct.of(1L, 1)
            )
        );

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(2, () -> {
            try {
                stockService.deductStock(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failCount.get()).isZero();

        Stock remainStock = stockRepository.findByProductId(1L);
        assertThat(remainStock.getQuantity()).isEqualTo(8);
    }

    @DisplayName("동시에 재고를 차감하는 경우 재고가 부족하면 예외가 발생한다.")
    @Test
    void deductStockWithPessimisticWriteLockWhenInsufficientStock() {
        // given
        Stock stock = Stock.create(1L, 1);
        stockRepository.save(stock);

        StockCommand.OrderProducts command = StockCommand.OrderProducts.of(
            List.of(
                StockCommand.OrderProduct.of(1L, 1)
            )
        );

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(2, () -> {
            try {
                stockService.deductStock(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Stock remainStock = stockRepository.findByProductId(1L);
        assertThat(remainStock.getQuantity()).isZero();
    }
}