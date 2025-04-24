package kr.hhplus.be.ecommerce.application.balance;

import kr.hhplus.be.ecommerce.domain.balance.Balance;
import kr.hhplus.be.ecommerce.domain.balance.BalanceRepository;
import kr.hhplus.be.ecommerce.domain.user.User;
import kr.hhplus.be.ecommerce.domain.user.UserRepository;
import kr.hhplus.be.ecommerce.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceFacadeConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private BalanceFacade balanceFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @DisplayName("동시성 - 모든 잔액 충전이 정상 처리 되어야 한다.")
    @Test
    void chargeBalanceConcurrency() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.create(user.getId(), 1_000L);
        balanceRepository.save(balance);

        BalanceCriteria.Charge criteria = BalanceCriteria.Charge.of(user.getId(), 10_000L);

        // when
        executeConcurrency(3, () -> balanceFacade.chargeBalance(criteria));

        // then
        Balance expectedBalance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(expectedBalance.getAmount()).isEqualTo(31_000L);
    }

}