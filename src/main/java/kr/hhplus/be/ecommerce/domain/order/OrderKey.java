package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.support.key.KeyGeneratable;
import kr.hhplus.be.ecommerce.support.key.KeyType;

import java.util.List;

import static kr.hhplus.be.ecommerce.support.key.KeyType.ORDER;

public class OrderKey implements KeyGeneratable {

    private final Long orderId;

    private OrderKey(Long orderId) {
        this.orderId = orderId;
    }

    public static OrderKey of(Long orderId) {
        return new OrderKey(orderId);
    }

    @Override
    public KeyType type() {
        return ORDER;
    }

    @Override
    public List<String> namespaces() {
        return List.of(orderId.toString());
    }
}
