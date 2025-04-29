package kr.hhplus.be.ecommerce.domain.balance;

import kr.hhplus.be.ecommerce.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceServiceConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    @DisplayName("잔액 충전 시, 동시에 충전 요청이 들어오면 하나만 성공해야 한다.")
    @Test
    void chargeBalanceWithOptimisticLock() {
        // given
        Long userId = 1L;
        Balance balance = Balance.create(userId);
        balanceRepository.save(balance);

        BalanceCommand.Charge command = BalanceCommand.Charge.of(userId, 1_000L);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(2, () -> {
            try {
                balanceService.chargeBalance(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Balance chargedBalance = balanceRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(chargedBalance.getAmount()).isEqualTo(1_000L);
    }

    @DisplayName("잔액 사용 시, 동시에 사용 요청이 들어오면 하나만 성공해야 한다.")
    @Test
    void useBalanceWithOptimisticLock() {
        // given
        Long userId = 1L;
        Balance balance = Balance.create(userId);
        balance.charge(1_000L);
        balanceRepository.save(balance);

        BalanceCommand.Use command = BalanceCommand.Use.of(userId, 500L);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(2, () -> {
            try {
                balanceService.useBalance(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Balance usedBalance = balanceRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(usedBalance.getAmount()).isEqualTo(500L);
    }

    @DisplayName("잔액 충전과 사용이 동시에 들어오면 하나만 수행 되어야 한다.")
    @Test
    void chargeAndUseBalanceWithOptimisticLock() {
        // given
        Long userId = 1L;
        Balance balance = Balance.create(userId);
        balance.charge(1_000L);
        balanceRepository.save(balance);

        BalanceCommand.Charge chargeCommand = BalanceCommand.Charge.of(userId, 500L);
        BalanceCommand.Use useCommand = BalanceCommand.Use.of(userId, 300L);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(List.of(
            () -> {
                try {
                    balanceService.chargeBalance(chargeCommand);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            },
            () -> {
                try {
                    balanceService.useBalance(useCommand);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            }
        ));

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}