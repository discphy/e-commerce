package kr.hhplus.be.ecommerce.infrastructure.payment.client;

import kr.hhplus.be.ecommerce.domain.payment.PaymentClient;
import kr.hhplus.be.ecommerce.domain.payment.PaymentInfo;
import org.springframework.stereotype.Component;

@Component
public class PaymentApiClient implements PaymentClient {

    @Override
    public void useBalance(Long userId, long amount) {

    }

    @Override
    public void useCoupon(Long userCouponId) {

    }

    @Override
    public PaymentInfo.Order getOrder(Long orderId) {
        return null;
    }

    @Override
    public void refundBalance(Long userId, long amount) {

    }

    @Override
    public void cancelCoupon(Long userCouponId) {

    }
}
