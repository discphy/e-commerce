package kr.hhplus.be.ecommerce.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private long price;

    @Enumerated(EnumType.STRING)
    private ProductSellingStatus sellStatus;

    @Builder
    private Product(Long id, String name, long price, ProductSellingStatus sellStatus) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sellStatus = sellStatus;
    }

    public static Product of(String name, long price, ProductSellingStatus sellStatus) {
        return Product.builder()
            .name(name)
            .price(price)
            .sellStatus(sellStatus)
            .build();
    }

    public boolean cannotSelling() {
        return sellStatus.cannotSelling();
    }
}
