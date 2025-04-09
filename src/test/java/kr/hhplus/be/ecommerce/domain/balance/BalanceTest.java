package kr.hhplus.be.ecommerce.domain.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;

class BalanceTest {

    @DisplayName("충전 최대 금액을 넘을 수 없다.")
    @Test
    void chargeCannotExceedMaxAmount() {
        // given
        Balance balance = Balance.create(anyLong(), 10_000_000L);

        // when & then
        assertThatThrownBy(() -> balance.charge(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void charge() {
        // given
        Balance balance = Balance.create(anyLong(), 1_000_000L);

        // when
        balance.charge(1_000_000L);

        // then
        assertThat(balance.getAmount()).isEqualTo(2_000_000L);
    }

}