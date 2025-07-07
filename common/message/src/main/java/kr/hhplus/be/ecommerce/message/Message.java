package kr.hhplus.be.ecommerce.message;

public interface Message {

    String getTopic();

    String getKey();

    String getPayload();
}
