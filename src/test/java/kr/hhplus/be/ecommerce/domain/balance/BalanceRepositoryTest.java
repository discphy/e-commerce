package kr.hhplus.be.ecommerce.domain.balance;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class BalanceRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private BalanceRepository balanceRepository;

    @DisplayName("잔액을 저장한다.")
    @Test
    void save() {
        // given
        Balance balance = Balance.create(1L, 1_000L);

        // when
        balanceRepository.save(balance);

        // then
        assertThat(balance.getId()).isNotNull();
    }

    @DisplayName("잔액이 없는 유저의 잔액을 조회한다.")
    @Test
    void findOptionalByUserId() {
        // when
        Optional<Balance> result = balanceRepository.findOptionalByUserId(1L);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("잔액이 있는 유저의 잔액을 조회한다.")
    @Test
    void findByUserId() {
        // given
        Balance balance = Balance.create(1L, 1_000L);
        balanceRepository.save(balance);

        // when
        Balance result = balanceRepository.findOptionalByUserId(balance.getUserId()).orElseThrow();

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAmount()).isEqualTo(balance.getAmount());
    }
}