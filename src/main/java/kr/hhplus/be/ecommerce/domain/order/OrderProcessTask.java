package kr.hhplus.be.ecommerce.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderProcessTask {

    COUPON_USED("쿠폰 사용"),
    BALANCE_USED("잔액 사용"),
    STOCK_DEDUCTED("재고 차감"),
    ;

    private final String description;

    public String toLowerCase() {
        return this.name().toLowerCase();
    }
}
