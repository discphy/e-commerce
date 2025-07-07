package kr.hhplus.be.ecommerce.balance.domain;

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

    private Long balanceId;

    @Enumerated(EnumType.STRING)
    private BalanceTransactionType transactionType;

    private long amount;

    @Builder
    private BalanceTransaction(Long id, Long balanceId, BalanceTransactionType transactionType, long amount) {
        this.id = id;
        this.balanceId = balanceId;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public static BalanceTransaction ofCharge(Balance balance, long amount) {
        return BalanceTransaction.builder()
            .balanceId(balance.getId())
            .transactionType(BalanceTransactionType.CHARGE)
            .amount(amount)
            .build();
    }

    public static BalanceTransaction ofUse(Balance balance, long amount) {
        return BalanceTransaction.builder()
            .balanceId(balance.getId())
            .transactionType(BalanceTransactionType.USE)
            .amount(-amount)
            .build();
    }

    public static BalanceTransaction ofRefund(Balance balance, long amount) {
        return BalanceTransaction.builder()
            .balanceId(balance.getId())
            .transactionType(BalanceTransactionType.REFUND)
            .amount(amount)
            .build();
    }
}
