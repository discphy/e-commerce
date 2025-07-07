package kr.hhplus.be.ecommerce.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_status_paid_at", columnList = "order_status, paid_at")
})
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long userCouponId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private long totalPrice;

    private long discountPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private LocalDateTime completedAt;

    @Builder
    private Order(Long userId, Long userCouponId, double discountRate, List<OrderProduct> orderProducts) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderStatus = OrderStatus.CREATED;

        orderProducts.forEach(this::addOrderProduct);

        long calculatedTotalPrice = calculateTotalPrice(orderProducts);
        long calculatedDiscountPrice = calculateDiscountPrice(calculatedTotalPrice, discountRate);

        this.totalPrice = calculatedTotalPrice - calculatedDiscountPrice;
        this.discountPrice = calculatedDiscountPrice;
    }

    public static Order create(Long userId, Long userCouponId, double discountRate, List<OrderProduct> orderProducts) {
        validateOrderProducts(orderProducts);

        return Order.builder()
            .userId(userId)
            .userCouponId(userCouponId)
            .discountRate(discountRate)
            .orderProducts(orderProducts)
            .build();
    }

    public void completed(LocalDateTime completedAt) {
        this.orderStatus = OrderStatus.COMPLETED;
        this.completedAt = completedAt;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELED;
    }

    private long calculateTotalPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
            .mapToLong(OrderProduct::getPrice)
            .sum();
    }

    private long calculateDiscountPrice(long totalPrice, double discountRate) {
        return (long) (totalPrice * discountRate);
    }

    private void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    private static void validateOrderProducts(List<OrderProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 없습니다.");
        }
    }
}
