package kr.hhplus.be.ecommerce.domain.balance;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceTransaction {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id")
    private Balance balance;

    @Enumerated(EnumType.STRING)
    private BalanceTransactionType transactionType;

    private long amount;

    @Builder
    private BalanceTransaction(Long id, Balance balance, BalanceTransactionType transactionType, long amount) {
        this.id = id;
        this.balance = balance;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public static BalanceTransaction ofCharge(Balance balance, long amount) {
        return BalanceTransaction.builder()
            .balance(balance)
            .transactionType(BalanceTransactionType.CHARGE)
            .amount(amount)
            .build();
    }

    public static BalanceTransaction ofUse(Balance balance, long amount) {
        return BalanceTransaction.builder()
            .balance(balance)
            .transactionType(BalanceTransactionType.USE)
            .amount(-amount)
            .build();
    }
}
