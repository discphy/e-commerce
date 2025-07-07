package kr.hhplus.be.ecommerce.message;


public interface MessageProducer {

    void send(Message message);

    void sendSync(Message message) throws Exception;
}
