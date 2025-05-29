package kr.hhplus.be.ecommerce.support.message;

public interface Message {

    String getTopic();

    String getKey();

    String getPayload();
}
