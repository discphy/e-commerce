package kr.hhplus.be.ecommerce.product.domain.product;

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

    public static Product create(String name, long price, ProductSellingStatus sellStatus) {
        validateName(name);
        validatePrice(price);
        validateSellStatus(sellStatus);

        return Product.builder()
            .name(name)
            .price(price)
            .sellStatus(sellStatus)
            .build();
    }

    public boolean cannotSelling() {
        return sellStatus.cannotSelling();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
    }

    private static void validatePrice(long price) {
        if (price <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }
    }

    private static void validateSellStatus(ProductSellingStatus sellStatus) {
        if (sellStatus == null) {
            throw new IllegalArgumentException("상품 판매 상태는 필수입니다.");
        }
    }
}
