package kr.hhplus.be.ecommerce.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public StockInfo.Stock getStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId);
        return StockInfo.Stock.of(stock.getId(), stock.getQuantity());
    }

    @Transactional
    public void deductStock(StockCommand.OrderProducts command) {
        command.getProducts().forEach(this::deductStock);
    }

    private void deductStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findByProductIdWithLock(command.getProductId());
        stock.deduct(command.getQuantity());
    }
}
