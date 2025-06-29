package kr.hhplus.be.ecommerce.domain.order;

public interface OrderEventPublisher {

    void created(OrderEvent.Created event);

    void completed(OrderEvent.Completed event);

    void completeFailed(OrderEvent.CompleteFailed event);
}
