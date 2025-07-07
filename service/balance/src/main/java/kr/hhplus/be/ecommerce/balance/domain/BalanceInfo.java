package kr.hhplus.be.ecommerce.balance.domain;

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

    @Getter
    public static class User {

        private final Long userId;
        private final String userName;

        private User(Long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public static User of(Long userId, String userName) {
            return new User(userId, userName);
        }
    }
}
