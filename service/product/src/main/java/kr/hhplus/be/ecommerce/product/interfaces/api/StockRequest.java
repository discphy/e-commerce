package kr.hhplus.be.ecommerce.product.interfaces.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.ecommerce.product.domain.stock.StockCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockRequest {

    @Getter
    @NoArgsConstructor
    public static class Deduct {

        @Valid
        @NotEmpty(message = "재고는 1개 이상이여야 합니다.")
        private List<Product> products;

        private Deduct(List<Product> products) {
            this.products = products;
        }

        public static Deduct of(List<Product> products) {
            return new Deduct(products);
        }

        public StockCommand.Deduct toCommand() {
            return StockCommand.Deduct.of(
                products.stream()
                    .map(p -> StockCommand.OrderProduct.of(p.getId(), p.getQuantity()))
                    .toList()
            );
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Restore {

        @Valid
        @NotEmpty(message = "재고는 1개 이상이여야 합니다.")
        private List<Product> products;

        private Restore(List<Product> products) {
            this.products = products;
        }

        public static Restore of(List<Product> products) {
            return new Restore(products);
        }

        public StockCommand.Restore toCommand() {
            return StockCommand.Restore.of(
                products.stream()
                    .map(p -> StockCommand.OrderProduct.of(p.getId(), p.getQuantity()))
                    .toList()
            );
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Product {

        @NotNull(message = "상품 ID는 필수입니다.")
        private Long id;

        @NotNull(message = "재고 수량은 필수입니다.")
        @Positive(message = "재고 수량은 양수여야 합니다.")
        private Integer quantity;

        private Product(Long id, Integer quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public static Product of(Long id, Integer quantity) {
            return new Product(id, quantity);
        }
    }
}
