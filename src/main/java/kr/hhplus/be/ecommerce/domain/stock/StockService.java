package kr.hhplus.be.ecommerce.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public StockInfo.Stock getStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId);
        return StockInfo.Stock.of(stock.getId(), stock.getQuantity());
    }

    public void deductStock(StockCommand.OrderProducts command) {
        command.getProducts().forEach(this::deductStock);
    }

    private void deductStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findByProductId(command.getProductId());
        stock.deduct(command.getQuantity());
    }
}
