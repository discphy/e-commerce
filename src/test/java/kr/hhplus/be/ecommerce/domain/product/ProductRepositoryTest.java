package kr.hhplus.be.ecommerce.domain.product;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
class ProductRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품 ID로 상품 조회시 존재해야 한다.")
    @Test
    void findByIdWhenProductDoseNotExist() {
        // given
        Long productId = 1L;

        // when & then
        assertThatThrownBy(() -> productRepository.findById(productId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("상품이 존재하지 않습니다.");
    }

    @DisplayName("상품 ID로 상품을 조회한다.")
    @Test
    void findById() {
        // given
        Product product = Product.create("상품명", 1_000L, ProductSellingStatus.SELLING);
        Product savedProduct = productRepository.save(product);

        // when
        Product result = productRepository.findById(savedProduct.getId());

        // then
        assertThat(result).isEqualTo(savedProduct);
        assertThat(result.getId()).isEqualTo(savedProduct.getId());
    }

    @DisplayName("상품 판매상태로 상품들을 조회한다.")
    @Test
    void findSellingStatusIn() {
        // given
        Product product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        Product targetProduct = Product.create("상품명2", 2_000L, ProductSellingStatus.HOLD);
        Product product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.STOP_SELLING);
        List.of(product1, targetProduct, product3).forEach(productRepository::save);

        List<ProductSellingStatus> holdStatus = List.of(ProductSellingStatus.HOLD);

        // when
        List<Product> results = productRepository.findSellingStatusIn(holdStatus);

        // then
        assertThat(results).hasSize(1)
            .extracting("name", "sellStatus")
            .containsExactlyInAnyOrder(
                tuple(targetProduct.getName(), targetProduct.getSellStatus())
            );
    }
}