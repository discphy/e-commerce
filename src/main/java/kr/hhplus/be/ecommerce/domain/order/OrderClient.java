package kr.hhplus.be.ecommerce.domain.order;

import java.util.List;

public interface OrderClient {

    OrderInfo.User getUser(Long userId);

    List<OrderInfo.Product> getProducts(List<OrderCommand.OrderProduct> command);

    OrderInfo.Coupon getUsableCoupon(Long userCouponId);
}
