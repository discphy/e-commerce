package kr.hhplus.be.ecommerce.interfaces.balance.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.ecommerce.domain.balance.BalanceCommand;
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
}
