package kr.hhplus.be.ecommerce.infrastructure.order.client;

import kr.hhplus.be.ecommerce.domain.order.OrderClient;
import kr.hhplus.be.ecommerce.domain.order.OrderCommand;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderApiClient implements OrderClient {

    @Override
    public List<OrderInfo.Product> getProducts(List<OrderCommand.OrderProduct> command) {
        return List.of(
            OrderInfo.Product.builder()
                .id(1L)
                .name("Sample Product")
                .price(10_000L)
                .quantity(100)
                .build(),
            OrderInfo.Product.builder()
                .id(2L)
                .name("Another Product")
                .price(20_000L)
                .quantity(50)
                .build()
        );
    }

    @Override
    public OrderInfo.Coupon getUsableCoupon(Long userCouponId) {
        return null;
    }

    @Override
    public void deductStock(List<OrderCommand.OrderProduct> products) {

    }

    @Override
    public void restoreStock(List<OrderCommand.OrderProduct> products) {

    }
}
