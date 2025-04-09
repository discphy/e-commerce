package kr.hhplus.be.ecommerce.domain.balance;

import kr.hhplus.be.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

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
}