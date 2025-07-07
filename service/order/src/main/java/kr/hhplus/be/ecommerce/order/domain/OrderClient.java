package kr.hhplus.be.ecommerce.order.domain;


import java.util.List;

public interface OrderClient {

    List<OrderInfo.Product> getProducts(List<OrderCommand.OrderProduct> command);

    OrderInfo.Coupon getUsableCoupon(Long userCouponId);

    void deductStock(List<OrderCommand.OrderProduct> products);

    void restoreStock(List<OrderCommand.OrderProduct> products);
}
