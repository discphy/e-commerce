package kr.hhplus.be.ecommerce.domain.balance;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "balance", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
public class Balance {

    private static final long MAX_BALANCE_AMOUNT = 10_000_000L;
    private static final long INIT_BALANCE_AMOUNT = 0L;

    @Id
    @Column(name = "balance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    private Long userId;

    private long amount;

    @Builder
    private Balance(Long id, Long userId, long amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    public static Balance create(Long userId) {
        return Balance.builder()
            .userId(userId)
            .amount(INIT_BALANCE_AMOUNT)
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
    }

    public void use(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }

        if (this.amount < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        this.amount -= amount;
    }
}
