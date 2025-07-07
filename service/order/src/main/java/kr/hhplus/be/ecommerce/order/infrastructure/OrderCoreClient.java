package kr.hhplus.be.ecommerce.order.infrastructure;

import kr.hhplus.be.ecommerce.client.api.coupon.CouponApiClient;
import kr.hhplus.be.ecommerce.client.api.coupon.CouponResponse;
import kr.hhplus.be.ecommerce.client.api.product.ProductApiClient;
import kr.hhplus.be.ecommerce.client.api.product.ProductResponse;
import kr.hhplus.be.ecommerce.client.api.product.StockRequest;
import kr.hhplus.be.ecommerce.order.domain.OrderClient;
import kr.hhplus.be.ecommerce.order.domain.OrderCommand;
import kr.hhplus.be.ecommerce.order.domain.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCoreClient implements OrderClient {

    private final CouponApiClient couponApiClient;
    private final ProductApiClient productApiClient;

    @Override
    public OrderInfo.Coupon getUsableCoupon(Long userCouponId) {
        CouponResponse.UserCoupon userCoupon = couponApiClient.getUsableCoupon(userCouponId).getData();
        return OrderInfo.Coupon.of(
            userCoupon.getUserCouponId(),
            userCoupon.getCouponId(),
            userCoupon.getCouponName(),
            userCoupon.getDiscountRate(),
            userCoupon.getIssuedAt()
        );
    }

    @Override
    public List<OrderInfo.Product> getProducts(List<OrderCommand.OrderProduct> command) {
        ProductResponse.Products products = productApiClient.getProducts(
            (long) command.size(),
            null,
            command.stream()
                .map(OrderCommand.OrderProduct::getProductId)
                .toList()
        ).getData();

        return products.getProducts().stream()
            .map(product -> OrderInfo.Product.of(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStock()
                )
            )
            .toList();
    }

    @Override
    public void deductStock(List<OrderCommand.OrderProduct> products) {
        productApiClient.deductStock(
            StockRequest.Deduct.of(
                products.stream()
                    .map(product -> StockRequest.Product.of(product.getProductId(), product.getQuantity()))
                    .toList()
            )
        );
    }

    @Override
    public void restoreStock(List<OrderCommand.OrderProduct> products) {
        productApiClient.restoreStock(
            StockRequest.Restore.of(
                products.stream()
                    .map(product -> StockRequest.Product.of(product.getProductId(), product.getQuantity()))
                    .toList()
            )
        );
    }
}
