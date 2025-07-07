package kr.hhplus.be.ecommerce.balance.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceCommand {

    @Getter
    public static class Charge {

        private final Long userId;
        private final Long amount;

        private Charge(Long userId, Long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Charge of(Long userId, Long amount) {
            return new Charge(userId, amount);
        }
    }

    @Getter
    public static class Use {

        private final Long userId;
        private final Long amount;

        private Use(Long userId, Long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Use of(Long userId, Long amount) {
            return new Use(userId, amount);
        }
    }

    @Getter
    public static class Refund {

        private final Long userId;
        private final Long amount;

        private Refund(Long userId, Long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Refund of(Long userId, Long amount) {
            return new Refund(userId, amount);
        }
    }
}
