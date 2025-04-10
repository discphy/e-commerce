package kr.hhplus.be.ecommerce.domain.balance;

import kr.hhplus.be.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BalanceServiceTest extends MockTestSupport {

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @DisplayName("잔고가 없으면, 잔고를 생성한다.")
    @Test
    void chargeBalanceIfNotExist() {
        // given
        BalanceCommand.Charge command = BalanceCommand.Charge.of(1L, 10_000L);

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.empty());

        // when
        balanceService.chargeBalance(command);

        // then
        verify(balanceRepository, times(1)).save(any(Balance.class));
    }

    @DisplayName("잔고가 있으면, 잔고를 충전한다.")
    @Test
    void chargeBalanceIfExist() {
        // given
        BalanceCommand.Charge command = mock(BalanceCommand.Charge.class);

        Balance balance = mock(Balance.class);
        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when
        balanceService.chargeBalance(command);

        // then
        verify(balance, times(1)).charge(command.getAmount());
        verify(balanceRepository, times(0)).save(any(Balance.class));
    }

    @DisplayName("잔고가 없으면, 잔고를 차감하지 못한다.")
    @Test
    void useBalanceIfNotExist() {
        // given
        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 10_000L);

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔고가 부족하면, 잔고를 차감하지 못한다.")
    @Test
    void useBalanceWithInsufficientBalance() {
        // given
        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 10_001L);

        Balance balance = Balance.create(1L, 10_000L);

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔고를 차감한다.")
    @Test
    void useBalance() {
        // given
        BalanceCommand.Use command = BalanceCommand.Use.of(1L, 10_000L);

        Balance balance = Balance.create(1L, 10_000L);

        when(balanceRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(balance));

        // when
        balanceService.useBalance(command);

        // then
        assertThat(balance.getAmount()).isZero();
    }
}