package kr.hhplus.be.ecommerce.domain.message;

public interface MessageClient {

    void sendOrder(MessageCommand.Order message);
}
