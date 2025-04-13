package kr.hhplus.be.ecommerce.application.order;

import kr.hhplus.be.ecommerce.domain.balance.BalanceCommand;
import kr.hhplus.be.ecommerce.domain.order.OrderCommand;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.payment.PaymentCommand;
import kr.hhplus.be.ecommerce.domain.product.ProductCommand;
import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.stock.StockCommand;
import kr.hhplus.be.ecommerce.domain.user.UserCouponCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCriteria {

    @Getter
    public static class OrderPayment {

        private final Long userId;
        private final Long couponId;
        private final List<OrderProduct> products;

        private OrderPayment(Long userId, Long couponId, List<OrderProduct> products) {
            this.userId = userId;
            this.couponId = couponId;
            this.products = products;
        }

        public static OrderPayment of(Long userId, Long couponId, List<OrderProduct> products) {
            return new OrderPayment(userId, couponId, products);
        }

        public ProductCommand.OrderProducts toProductCommand() {
            return ProductCommand.OrderProducts.of(
                products.stream()
                    .map(o -> ProductCommand.OrderProduct.of(o.getProductId(), o.getQuantity()))
                    .toList()
            );
        }

        public OrderCommand.Create toOrderCommand(Long userCouponId, double discountRage, ProductInfo.OrderProducts productInfo) {
            List<OrderCommand.OrderProduct> orderProducts = productInfo.getProducts().stream()
                .map(p -> OrderCommand.OrderProduct.builder()
                    .productId(p.getProductId())
                    .productName(p.getProductName())
                    .productPrice(p.getProductPrice())
                    .quantity(p.getQuantity())
                    .build()
                ).toList();

            return OrderCommand.Create.of(userId, userCouponId, discountRage, orderProducts);
        }

        public UserCouponCommand.UsableCoupon toCouponCommand() {
            return UserCouponCommand.UsableCoupon.of(userId, couponId);
        }

        public BalanceCommand.Use toBalanceCommand(long totalPrice) {
            return BalanceCommand.Use.of(userId, totalPrice);
        }

        public StockCommand.OrderProducts toStockCommand() {
            return StockCommand.OrderProducts.of(
                products.stream()
                    .map(o -> StockCommand.OrderProduct.of(o.getProductId(), o.getQuantity()))
                    .toList()
            );
        }

        public PaymentCommand.Payment toPaymentCommand(OrderInfo.Order order) {
            return PaymentCommand.Payment.of(order.getOrderId(), userId, order.getTotalPrice());
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long productId;
        private final int quantity;

        private OrderProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, int quantity) {
            return new OrderProduct(productId, quantity);
        }
    }
}
