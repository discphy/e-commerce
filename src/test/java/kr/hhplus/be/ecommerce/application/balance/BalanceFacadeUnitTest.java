package kr.hhplus.be.ecommerce.application.balance;

import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import kr.hhplus.be.ecommerce.domain.balance.BalanceInfo;
import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BalanceFacadeUnitTest extends MockTestSupport {

    @InjectMocks
    private BalanceFacade balanceFacade;

    @Mock
    private UserService userService;

    @Mock
    private BalanceService balanceService;

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargeBalance() {
        // given
        BalanceCriteria.Charge criteria = mock(BalanceCriteria.Charge.class);

        // when
        balanceFacade.chargeBalance(criteria);

        // then
        InOrder inOrder = inOrder(userService, balanceService);
        inOrder.verify(userService, times(1)).getUser(criteria.getUserId());
        inOrder.verify(balanceService, times(1)).chargeBalance(criteria.toCommand());
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() {
        // given
        Long userId = anyLong();

        when(balanceService.getBalance(userId))
            .thenReturn(BalanceInfo.Balance.of(10_000L));

        // when
        BalanceResult.Balance balance = balanceFacade.getBalance(userId);

        // then
        InOrder inOrder = inOrder(userService, balanceService);
        inOrder.verify(userService, times(1)).getUser(userId);
        inOrder.verify(balanceService, times(1)).getBalance(userId);
        assertThat(balance.getAmount()).isEqualTo(10_000L);
    }
}