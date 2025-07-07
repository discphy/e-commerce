package kr.hhplus.be.ecommerce.balance.interfaces.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.ecommerce.balance.domain.BalanceCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceRequest {

    @Getter
    @NoArgsConstructor
    public static class Charge {

        @NotNull(message = "잔액은 필수 입니다.")
        @Positive(message = "잔액은 양수여야 합니다.")
        private Long amount;

        private Charge(Long amount) {
            this.amount = amount;
        }

        public static Charge of(Long amount) {
            return new Charge(amount);
        }

        public BalanceCommand.Charge toCommand(Long userId) {
            return BalanceCommand.Charge.of(userId, amount);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Use {

        @NotNull(message = "잔액은 필수 입니다.")
        @Positive(message = "잔액은 양수여야 합니다.")
        private Long amount;

        private Use(Long amount) {
            this.amount = amount;
        }

        public static Charge of(Long amount) {
            return new Charge(amount);
        }

        public BalanceCommand.Use toCommand(Long userId) {
            return BalanceCommand.Use.of(userId, amount);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Refund {

        @NotNull(message = "잔액은 필수 입니다.")
        @Positive(message = "잔액은 양수여야 합니다.")
        private Long amount;

        private Refund(Long amount) {
            this.amount = amount;
        }

        public static Charge of(Long amount) {
            return new Charge(amount);
        }

        public BalanceCommand.Refund toCommand(Long userId) {
            return BalanceCommand.Refund.of(userId, amount);
        }
    }
}
