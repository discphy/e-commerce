package kr.hhplus.be.ecommerce.domain.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long orderId;

    private long amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private LocalDateTime paidAt;

    @Builder
    private Payment(Long id,
                    Long userId,
                    Long orderId,
                    long amount,
                    PaymentMethod paymentMethod,
                    PaymentStatus paymentStatus,
                    LocalDateTime paidAt) {
        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
    }

    public static Payment create(Long userId, Long orderId, long amount) {
        return Payment.builder()
            .userId(userId)
            .orderId(orderId)
            .amount(amount)
            .paymentMethod(PaymentMethod.UNKNOWN)
            .paymentStatus(PaymentStatus.READY)
            .build();
    }

    public void pay() {
        if (paymentStatus.cannotPayable()) {
            throw new IllegalStateException("결제 가능 상태가 아닙니다.");
        }

        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }
}
