package kr.hhplus.be.ecommerce.domain.order;

public interface OrderEventPublisher {

    void paid(OrderEvent.Paid event);
}
