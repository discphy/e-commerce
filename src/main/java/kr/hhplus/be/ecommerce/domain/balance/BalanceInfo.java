package kr.hhplus.be.ecommerce.domain.balance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceInfo {

    @Getter
    public static class Balance {

        private final long amount;

        private Balance(long amount) {
            this.amount = amount;
        }

        public static Balance of(long amount) {
            return new Balance(amount);
        }
    }
}
