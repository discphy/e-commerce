package kr.hhplus.be.ecommerce.application.balance;

import kr.hhplus.be.ecommerce.MockTestSupport;
import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class BalanceFacadeTest extends MockTestSupport {

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
}