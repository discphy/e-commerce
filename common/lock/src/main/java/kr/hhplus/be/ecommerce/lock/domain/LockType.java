package kr.hhplus.be.ecommerce.lock.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LockType {

    COUPON("쿠폰"),
    ORDER("주문"),
    ;

    private final String description;

    public String createKey(String key) {
        return this.name().toLowerCase() + ":" + key;
    }
}
