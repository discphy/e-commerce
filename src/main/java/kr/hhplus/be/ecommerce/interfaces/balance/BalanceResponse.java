package kr.hhplus.be.ecommerce.interfaces.balance;

import kr.hhplus.be.ecommerce.application.balance.BalanceResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceResponse {

    @Getter
    @NoArgsConstructor
    public static class BalanceV1 {

        private Long amount;

        private BalanceV1(Long amount) {
            this.amount = amount;
        }

        public static BalanceV1 of(BalanceResult.Balance balance) {
            return new BalanceV1(balance.getAmount());
        }
    }
}
