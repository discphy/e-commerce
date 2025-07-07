package kr.hhplus.be.ecommerce.balance.interfaces.api;

import kr.hhplus.be.ecommerce.balance.domain.BalanceInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceResponse {

    @Getter
    @NoArgsConstructor
    public static class Balance {

        private Long amount;

        private Balance(Long amount) {
            this.amount = amount;
        }

        public static Balance of(BalanceInfo.Balance balance) {
            return new Balance(balance.getAmount());
        }
    }
}
