package kr.hhplus.be.ecommerce.payment.infrastructure;

import kr.hhplus.be.ecommerce.client.api.balance.BalanceApiClient;
import kr.hhplus.be.ecommerce.client.api.balance.BalanceRequest;
import kr.hhplus.be.ecommerce.client.api.coupon.CouponApiClient;
import kr.hhplus.be.ecommerce.client.api.order.OrderApiClient;
import kr.hhplus.be.ecommerce.client.api.order.OrderResponse;
import kr.hhplus.be.ecommerce.payment.domain.PaymentClient;
import kr.hhplus.be.ecommerce.payment.domain.PaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCoreClient implements PaymentClient {

    private final OrderApiClient orderApiClient;
    private final CouponApiClient couponApiClient;
    private final BalanceApiClient balanceApiClient;

    @Override
    public PaymentInfo.Order getOrder(Long orderId) {
        OrderResponse.Order order = orderApiClient.getOrder(orderId).getData();
        return PaymentInfo.Order.of(
            order.getOrderId(),
            order.getUserId(),
            order.getUserCouponId(),
            order.getTotalPrice()
        );
    }

    @Override
    public void useCoupon(Long userCouponId) {
        couponApiClient.useCoupon(userCouponId);
    }

    @Override
    public void cancelCoupon(Long userCouponId) {
        couponApiClient.cancelCoupon(userCouponId);
    }

    @Override
    public void useBalance(Long userId, long amount) {
        balanceApiClient.useBalance(userId, BalanceRequest.Use.of(amount));
    }

    @Override
    public void refundBalance(Long userId, long amount) {
        balanceApiClient.refundBalance(userId, BalanceRequest.Refund.of(amount));
    }
}
