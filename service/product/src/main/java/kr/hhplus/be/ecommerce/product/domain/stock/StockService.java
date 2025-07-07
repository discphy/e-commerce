package kr.hhplus.be.ecommerce.product.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public StockInfo.Stock getStock(Long productId) {
        Stock stock = stockRepository.findByProductId(productId);
        return StockInfo.Stock.of(stock.getId(), stock.getQuantity());
    }

    @Transactional
    public void deductStock(StockCommand.Deduct command) {
        command.getProducts().forEach(this::deductStock);
    }

    @Transactional
    public void restoreStock(StockCommand.Restore command) {
        command.getProducts().forEach(this::restoreStock);
    }

    private void deductStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findByProductIdWithLock(command.getProductId());
        stock.deduct(command.getQuantity());
    }

    private void restoreStock(StockCommand.OrderProduct command) {
        Stock stock = stockRepository.findByProductIdWithLock(command.getProductId());
        stock.restore(command.getQuantity());
    }
}
