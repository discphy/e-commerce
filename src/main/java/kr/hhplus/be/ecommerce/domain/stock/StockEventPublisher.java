package kr.hhplus.be.ecommerce.domain.stock;

public interface StockEventPublisher {

    void deducted(StockEvent.Deducted event);

    void deductFailed(StockEvent.DeductFailed event);
}
