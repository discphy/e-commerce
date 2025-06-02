package kr.hhplus.be.ecommerce.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Paid {

        private Long paymentId;
        private Long orderId;
        private Long userId;
        private long totalPrice;

        public static Paid of(Long paymentId, Long orderId, Long userId, long totalPrice) {
            return new Paid(paymentId, orderId, userId, totalPrice);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayFailed {

        private Long orderId;

        public static PayFailed of(Long orderId) {
            return new PayFailed(orderId);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Canceled {

        private Long orderId;

        public static Canceled of(Long orderId) {
            return new Canceled(orderId);
        }
    }
}
