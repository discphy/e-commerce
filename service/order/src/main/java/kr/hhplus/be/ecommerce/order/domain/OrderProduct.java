package kr.hhplus.be.ecommerce.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_product", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id")
})
public class OrderProduct {

    @Id
    @Column(name = "order_product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    private String productName;

    private long unitPrice;

    private int quantity;

    @Builder
    private OrderProduct(Long id, Order order, Long productId, String productName, long unitPrice, int quantity) {
        this.id = id;
        this.order = order;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public static OrderProduct create(Long productId, String productName, long unitPrice, int quantity) {
        return OrderProduct.builder()
            .productId(productId)
            .productName(productName)
            .unitPrice(unitPrice)
            .quantity(quantity)
            .build();
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public long getPrice() {
        return unitPrice * quantity;
    }
}
