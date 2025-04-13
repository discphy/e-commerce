package kr.hhplus.be.ecommerce.domain.balance;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
class BalanceServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    @DisplayName("잔고 충전 시, 잔고가 존재하면 충전 금액을 추가한다.")
    @Test
    void chargeBalanceWhenBalanceExists() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.create(userId, 10_000L);
        balanceRepository.save(existingBalance);

        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 5_000L);

        // when
        balanceService.chargeBalance(command);

        // then
        Balance updatedBalance = balanceRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(updatedBalance.getAmount()).isEqualTo(15_000L);
    }

    @DisplayName("잔고 충전 시, 잔고가 존재할 때 충전 금액이 양수이어야 한다.")
    @Test
    void chargeBalanceWhenAmountIsNotPositive() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.create(userId, 10_000L);
        balanceRepository.save(existingBalance);

        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, -5_000L);

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 충전 시, 최대 금액을 초과하면 예외를 발생시킨다.")
    @Test
    void chargeBalanceWhenExceedsMaxAmount() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.create(userId, 10_000_000L);
        balanceRepository.save(existingBalance);

        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 1L);

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔고 충전 시, 잔고가 없으면 새 잔고를 생성한다.")
    @Test
    void chargeBalanceWhenBalanceDoesNotExist() {
        // given
        Long userId = 1L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 5_000L);

        // when
        balanceService.chargeBalance(command);

        // then
        Balance newBalance = balanceRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(newBalance.getAmount()).isEqualTo(5_000L);
    }

    @DisplayName("잔고 생성 시, 저장 금액이 양수이어야 한다.")
    @Test
    void createBalanceWhenAmountIsNotPositive() {
        // given
        Long userId = 1L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, -5_000L);

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("초기 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 생성 시, 최대 금액을 초과하면 예외를 발생시킨다.")
    @Test
    void createBalanceWhenExceedsMaxAmount() {
        // given
        Long userId = 1L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 10_000_001L);

        // when & then
        assertThatThrownBy(() -> balanceService.chargeBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔고 사용 시, 잔고가 존재하면 사용 금액을 차감한다.")
    @Test
    void useBalanceWhenBalanceExists() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.create(userId, 10_000L);
        balanceRepository.save(existingBalance);

        BalanceCommand.Use command = BalanceCommand.Use.of(userId, 5_000L);

        // when
        balanceService.useBalance(command);

        // then
        Balance updatedBalance = balanceRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(updatedBalance.getAmount()).isEqualTo(5_000L);
    }

    @DisplayName("잔고 사용 시, 잔고가 없으면 예외를 발생시킨다.")
    @Test
    void useBalanceWhenBalanceDoseNotExist() {
        // given
        Long userId = 1L;
        BalanceCommand.Use command = BalanceCommand.Use.of(userId, 5_000L);

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 존재하지 않습니다.");
    }

    @DisplayName("잔고 사용 시, 사용 금액은 양수여야 한다.")
    @Test
    void useBalanceWhenAmountIsNotPositive() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.create(userId, 10_000L);
        balanceRepository.save(existingBalance);

        BalanceCommand.Use command = BalanceCommand.Use.of(userId, -5_000L);

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 사용 시, 잔고 금액은 충분해야한다.")
    @Test
    void useBalanceWhenInsufficientBalance() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.create(userId, 5_000L);
        balanceRepository.save(existingBalance);

        BalanceCommand.Use command = BalanceCommand.Use.of(userId, 5_001L);

        // when & then
        assertThatThrownBy(() -> balanceService.useBalance(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔고 충전 & 사용 시, 트랜잭션 내역을 저장한다.")
    @Test
    void saveBalanceTransactionAfterChargeBalanceAndUseBalance() {
        // given
        Long userId = 1L;
        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 5_000L);
        balanceService.chargeBalance(command);
        BalanceCommand.Use useCommand = BalanceCommand.Use.of(userId, 2_000L);
        balanceService.useBalance(useCommand);

        // when
        Balance balance = balanceRepository.findOptionalByUserId(userId).orElseThrow();

        // then
        assertThat(balance.getBalanceTransactions()).hasSize(2)
            .extracting("amount", "transactionType")
            .containsExactly(
                tuple(5_000L, BalanceTransactionType.CHARGE),
                tuple(-2_000L, BalanceTransactionType.USE)
            );
    }

    @DisplayName("잔고 조회 시, 잔고가 존재하면 잔고 정보를 반환한다.")
    @Test
    void getBalanceWhenBalanceExists() {
        // given
        Long userId = 1L;
        Balance existingBalance = Balance.create(userId, 10_000L);
        balanceRepository.save(existingBalance);

        // when
        BalanceInfo.Balance balance = balanceService.getBalance(userId);

        // then
        assertThat(balance.getAmount()).isEqualTo(10_000L);
    }

    @DisplayName("잔고 조회 시, 잔고가 없으면 빈 잔고 정보를 반환한다.")
    @Test
    void getBalanceWhenBalanceDoseNotExist() {
        // given
        Long userId = 1L;

        // when
        BalanceInfo.Balance balance = balanceService.getBalance(userId);

        // then
        assertThat(balance.getAmount()).isZero();
    }
}