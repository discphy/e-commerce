package kr.hhplus.be.ecommerce.order.domain;

public interface OrderEventPublisher {

    void created(OrderEvent.Created event);

    void completed(OrderEvent.Completed event);

    void completeFailed(OrderEvent.CompleteFailed event);
}
