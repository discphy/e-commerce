package kr.hhplus.be.ecommerce.balance.domain;

import kr.hhplus.be.ecommerce.balance.support.ConcurrencyTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BalanceServiceConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    @MockitoBean
    private BalanceClient balanceClient;

    private BalanceInfo.User user;

    @BeforeEach
    void setUp() {
        user = BalanceInfo.User.of(1L, "항플");

        when(balanceClient.getUser(user.getUserId()))
            .thenReturn(user);
    }

    @DisplayName("잔액 충전 시, 동시에 충전 요청이 들어오면 하나만 성공해야 한다.")
    @Test
    void chargeBalanceWithOptimisticLock() {
        // given
        Balance balance = Balance.create(user.getUserId());
        balanceRepository.save(balance);

        BalanceCommand.Charge command = BalanceCommand.Charge.of(user.getUserId(), 1_000L);

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

        Balance chargedBalance = balanceRepository.findOptionalByUserId(user.getUserId()).orElseThrow();
        assertThat(chargedBalance.getAmount()).isEqualTo(1_000L);
    }

    @DisplayName("잔액 사용 시, 동시에 사용 요청이 들어오면 하나만 성공해야 한다.")
    @Test
    void useBalanceWithOptimisticLock() {
        // given
        Balance balance = Balance.create(user.getUserId());
        balance.charge(1_000L);
        balanceRepository.save(balance);

        BalanceCommand.Use command = BalanceCommand.Use.of(user.getUserId(), 500L);

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

        Balance usedBalance = balanceRepository.findOptionalByUserId(user.getUserId()).orElseThrow();
        assertThat(usedBalance.getAmount()).isEqualTo(500L);
    }

    @DisplayName("잔액 충전과 사용이 동시에 들어오면 하나만 수행 되어야 한다.")
    @Test
    void chargeAndUseBalanceWithOptimisticLock() {
        // given
        Balance balance = Balance.create(user.getUserId());
        balance.charge(1_000L);
        balanceRepository.save(balance);

        BalanceCommand.Charge chargeCommand = BalanceCommand.Charge.of(user.getUserId(), 500L);
        BalanceCommand.Use useCommand = BalanceCommand.Use.of(user.getUserId(), 300L);

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