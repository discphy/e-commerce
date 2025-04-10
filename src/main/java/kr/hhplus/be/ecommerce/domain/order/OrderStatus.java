package kr.hhplus.be.ecommerce.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    CREATED("주문생성"),
    PAID("결제완료"),
    ;

    private final String description;

    private static final List<OrderStatus> NON_PAYABLE_STATUSES = List.of(PAID);

    public boolean cannotPayable() {
        return NON_PAYABLE_STATUSES.contains(this);
    }
}
