package kr.hhplus.be.ecommerce.application.rank;

import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RankFacadeUnitTest extends MockTestSupport {

    @InjectMocks
    private RankFacade rankFacade;

    @Mock
    private RankService rankService;

    @Mock
    private ProductService productService;

    @DisplayName("최근 3일 가장 많이 팔린 상위 상품 5개를 조회한다.")
    @Test
    void getPopularProducts() {
        // given
        RankInfo.PopularProducts rankPopularProducts = RankInfo.PopularProducts.of(List.of(
            RankInfo.PopularProduct.of(1L, 120L),  // 1등 상품
            RankInfo.PopularProduct.of(2L, 95L),   // 2등 상품
            RankInfo.PopularProduct.of(3L, 87L),   // 3등 상품
            RankInfo.PopularProduct.of(4L, 76L),   // 4등 상품
            RankInfo.PopularProduct.of(5L, 65L)   // 5등 상품
        ));
        when(rankService.getPopularSellRank(any()))
            .thenReturn(rankPopularProducts);

        ProductInfo.Products products = ProductInfo.Products.of(List.of(
            ProductInfo.Product.builder()
                .productId(1L)
                .productName("상품명1")
                .productPrice(1_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(2L)
                .productName("상품명2")
                .productPrice(2_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(3L)
                .productName("상품명3")
                .productPrice(3_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(4L)
                .productName("상품명4")
                .productPrice(4_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(5L)
                .productName("상품명5")
                .productPrice(5_000L)
                .build()
        ));

        when(productService.getProducts(any()))
            .thenReturn(products);

        // when
        RankResult.PopularProducts popularProducts = rankFacade.getPopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        // then
        InOrder inOrder = inOrder(rankService, productService);
        inOrder.verify(rankService, times(1)).getPopularSellRank(any());
        inOrder.verify(productService, times(1)).getProducts(any());

        assertThat(popularProducts.getProducts()).hasSize(5)
            .extracting("productId", "productName", "productPrice")
            .containsExactly(
                tuple(1L, "상품명1", 1_000L),
                tuple(2L, "상품명2", 2_000L),
                tuple(3L, "상품명3", 3_000L),
                tuple(4L, "상품명4", 4_000L),
                tuple(5L, "상품명5", 5_000L)
            );
    }

}