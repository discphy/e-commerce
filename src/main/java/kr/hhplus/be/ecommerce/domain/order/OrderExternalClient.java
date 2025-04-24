package kr.hhplus.be.ecommerce.domain.order;

public interface OrderExternalClient {

    void sendOrderMessage(Order order);

}
