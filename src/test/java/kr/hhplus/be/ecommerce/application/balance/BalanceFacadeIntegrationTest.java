package kr.hhplus.be.ecommerce.application.balance;

import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.domain.balance.Balance;
import kr.hhplus.be.ecommerce.domain.balance.BalanceRepository;
import kr.hhplus.be.ecommerce.domain.user.User;
import kr.hhplus.be.ecommerce.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class BalanceFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private BalanceFacade balanceFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.builder()
            .userId(user.getId())
            .amount(100_000L)
            .build();
        balanceRepository.save(balance);
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargeBalance() {
        // given
        BalanceCriteria.Charge criteria = BalanceCriteria.Charge.of(user.getId(), 10_000L);

        // when
        balanceFacade.chargeBalance(criteria);

        // then
        Balance balance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(balance.getAmount()).isEqualTo(110_000L);
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getBalance() {
        // given
        Long userId = user.getId();

        // when
        BalanceResult.Balance balance = balanceFacade.getBalance(userId);

        // then
        assertThat(balance.getAmount()).isEqualTo(100_000L);
    }
}