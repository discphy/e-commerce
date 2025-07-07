package kr.hhplus.be.ecommerce.balance.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BalanceTransactionType {

    CHARGE("충전"),
    USE("사용"),
    REFUND("환불"),
    ;
    private final String description;
}
