package kr.hhplus.be.ecommerce.application.order;

import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.coupon.CouponInfo;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.payment.PaymentService;
import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.stock.StockService;
import kr.hhplus.be.ecommerce.domain.user.UserCouponInfo;
import kr.hhplus.be.ecommerce.domain.user.UserCouponService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final ProductService productService;
    private final UserCouponService userCouponService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final BalanceService balanceService;
    private final StockService stockService;
    private final PaymentService paymentService;

    private static final double NOT_DISCOUNT_RATE = 0.0;

    public void orderPayment(OrderCriteria.OrderPayment criteria) {
        // 주문
        userService.getUser(criteria.getUserId());
        ProductInfo.OrderProducts orderProducts = productService.getOrderProducts(criteria.toProductCommand());

        Optional<Long> optionalCouponId = Optional.ofNullable(criteria.getCouponId());

        Optional<UserCouponInfo.UsableCoupon> optionalUsableCoupon = optionalCouponId
            .map(id -> userCouponService.getUsableCoupon(criteria.toCouponCommand()));

        Optional<CouponInfo.Coupon> optionalCoupon = optionalCouponId
            .map(couponService::getCoupon);

        OrderInfo.Order order = orderService.createOrder(
            criteria.toOrderCommand(
                optionalUsableCoupon.map(UserCouponInfo.UsableCoupon::getUserCouponId).orElse(null),
                optionalCoupon.map(CouponInfo.Coupon::getDiscountRate).orElse(NOT_DISCOUNT_RATE),
                orderProducts
            )
        );

        // 결제
        balanceService.useBalance(criteria.toBalanceCommand(order.getTotalPrice()));
        optionalUsableCoupon.ifPresent(coupon -> userCouponService.useUserCoupon(coupon.getUserCouponId()));
        stockService.deductStock(criteria.toStockCommand());
        paymentService.pay(criteria.toPaymentCommand(order));
        orderService.paidOrder(order.getOrderId());
    }
}
