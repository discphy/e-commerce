package kr.hhplus.be.ecommerce.support.message;

public interface MessageProducer {

    void send(Message message);

    void sendSync(Message message) throws Exception;
}
