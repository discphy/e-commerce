package kr.hhplus.be.ecommerce.domain.balance;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance {

    private static final long MAX_BALANCE_AMOUNT = 10_000_000L;

    @Id
    @Column(name = "balance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private long amount;

    @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
    private List<BalanceTransaction> balanceTransactions = new ArrayList<>();

    @Builder
    private Balance(Long id, Long userId, long amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;

        addChargeTransaction(amount);
    }

    public static Balance create(Long userId, Long amount) {
        validateAmount(amount);
        return Balance.builder()
            .userId(userId)
            .amount(amount)
            .build();
    }

    public void charge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        if (this.amount + amount > MAX_BALANCE_AMOUNT) {
            throw new IllegalArgumentException("최대 금액을 초과할 수 없습니다.");
        }

        this.amount += amount;
        addChargeTransaction(amount);
    }

    public void use(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }

        if (this.amount < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        this.amount -= amount;
        addUseTransaction(amount);
    }

    private void addChargeTransaction(long amount) {
        BalanceTransaction transaction = BalanceTransaction.ofCharge(this, amount);
        this.balanceTransactions.add(transaction);
    }

    private void addUseTransaction(long amount) {
        BalanceTransaction transaction = BalanceTransaction.ofUse(this, amount);
        this.balanceTransactions.add(transaction);
    }

    private static void validateAmount(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("초기 금액은 0보다 커야 합니다.");
        }
    }
}
