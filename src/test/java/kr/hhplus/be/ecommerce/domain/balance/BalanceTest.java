package kr.hhplus.be.ecommerce.domain.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceTest {

    @DisplayName("충전 금액은 0보다 커야한다.")
    @Test
    void chargeWithNotPositiveAmount() {
        // given
        Balance balance = Balance.create(1L);

        // when & then
        assertThatThrownBy(() -> balance.charge(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");
    }

    @DisplayName("충전 최대 금액을 넘을 수 없다.")
    @Test
    void chargeCannotExceedMaxAmount() {
        // given
        Balance balance = Balance.builder()
            .userId(1L)
            .amount(10_000_000L)
            .build();

        // when & then
        assertThatThrownBy(() -> balance.charge(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void charge() {
        // given
        Balance balance = Balance.builder()
            .userId(1L)
            .amount(1_000_000L)
            .build();

        // when
        balance.charge(1_000_000L);

        // then
        assertThat(balance.getAmount()).isEqualTo(2_000_000L);
    }

    @DisplayName("사용 금액은 0보다 커야한다.")
    @Test
    void useWithNotPositiveAmount() {
        // given
        Balance balance = Balance.builder()
            .userId(1L)
            .amount(1_000_000L)
            .build();

        // when & then
        assertThatThrownBy(() -> balance.use(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고가 부족할 경우 차감할 수 없다.")
    @Test
    void useCannotInsufficientAmount() {
        // given
        Balance balance = Balance.builder()
            .userId(1L)
            .amount(1_000_000L)
            .build();

        // when & then
        assertThatThrownBy(() -> balance.use(1_000_001L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔고를 차감한다.")
    @Test
    void use() {
        // given
        Balance balance = Balance.builder()
            .userId(1L)
            .amount(1_000_000L)
            .build();

        // when
        balance.use(1_000_000L);

        // then
        assertThat(balance.getAmount()).isZero();
    }
}