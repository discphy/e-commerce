package kr.hhplus.be.ecommerce.domain.payment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCommand {

    @Getter
    public static class Payment {

        private final Long orderId;
        private final Long userId;
        private final long amount;

        @Builder
        private Payment(Long orderId, Long userId, long amount) {
            this.orderId = orderId;
            this.userId = userId;
            this.amount = amount;
        }

        public static Payment of(Long orderId, Long userId, long amount) {
            return new Payment(orderId, userId, amount);
        }
    }
}
