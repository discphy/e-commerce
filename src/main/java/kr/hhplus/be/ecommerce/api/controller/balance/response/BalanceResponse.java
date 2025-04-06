package kr.hhplus.be.ecommerce.api.controller.balance.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BalanceResponse {

    private Long amount;

    private BalanceResponse(Long amount) {
        this.amount = amount;
    }

    public static BalanceResponse of(Long amount) {
        return new BalanceResponse(amount);
    }
}
