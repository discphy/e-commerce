package kr.hhplus.be.ecommerce.api.controller.balance.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BalanceUpdateRequest {

    @NotNull(message = "잔액은 필수 입니다.")
    @Positive(message = "잔액은 양수여야 합니다.")
    private Long amount;

    private BalanceUpdateRequest(Long amount) {
        this.amount = amount;
    }

    public static BalanceUpdateRequest of(Long amount) {
        return new BalanceUpdateRequest(amount);
    }
}
