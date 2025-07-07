package kr.hhplus.be.ecommerce.client.api.balance;

public class BalanceRequest {

    public record Charge(
        Long amount
    ) {
    }

    public record Use(
        Long amount
    ) {
        public static Use of(Long amount) {
            return new Use(amount);
        }
    }

    public record Refund(
        Long amount
    ) {
        public static Refund of(Long amount) {
            return new Refund(amount);
        }
    }
}
