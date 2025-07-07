package kr.hhplus.be.ecommerce.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    CREATED("주문생성"),
    COMPLETED("주문완료"),
    CANCELED("주문취소"),
    ;

    private final String description;

}
